import {useState, useEffect} from "react";

import "../styles/Form.css"

const apiUrl = 'https://www.ratemyprofessors.com/graphql';
const parameter = (query, variables) => {
    const token = "Basic dGVzdDp0ZXN0";

    const payload = {
        query: query, 
        variables: variables
    };

    return {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': token,
        },
        body: JSON.stringify(payload),
    };
};

function capitalize(str) {
    return str.replace(/\b\w/g, char => char.toUpperCase());
}

const Form = () => {
    const [canInteract, setCanInteract] = useState(true);
    const [university, setUniversity] = useState("");
    const [profByDept, setProfByDept] = useState([]);
    const [courses, setCourses] = useState([]);

    const addCourse = (course) => {
        setCourses((prevCourses) => [...prevCourses, course]);
    };

    const deleteCourse = (index) =>{
        const updatedCourses = courses.filter((course, i) => i !== index); //research
        setCourses(updatedCourses);
    }

    const addProfByDept = (profs) => {
        setProfByDept((prevProfs) => [...prevProfs, profs]);
    }

    return (
        <div id="form-container">
            <SelectUniversity 
                canInteract = {canInteract}
                setCanInteract = {setCanInteract}
                setUniversity = {setUniversity}/>
            
            {university &&
                <SelectCourse
                    canInteract = {canInteract}
                    setCanInteract = {setCanInteract}
                    loaded = {true}
                    courses = {courses} 
                    add = {addCourse} 
                    deleteCourse = {deleteCourse}
                    id = {university}
                    addProfs = {addProfByDept}/>
            }

            {university && courses.length > 0 &&
                <SelectProfessor 
                    university = {university} 
                    courses = {courses} 
                    dataSet = {profByDept}/>
            }
        </div>
    );
}

const SelectUniversity = ({ canInteract, setCanInteract, setUniversity }) => {
    const [_university, _setUniversity] = useState("");             // Value of input box, changes on input change
    const [options, setOptions] = useState([]);                     // Array of university name autocompletes
    const [finalizedOption, setFinalizedOption] = useState(false);  // Boolean for if university option was chosen

    const getUniversities = async(event) => {
        event.preventDefault();

        const query = `query SchoolSearchResultsPageQuery($query: SchoolSearchQuery!) { search: newSearch { schools(query: $query) { edges { node { id name } } } } } `;
        const variables = {query: {text: _university}};

        try {
            setCanInteract(false);

            const response = await fetch(apiUrl, parameter(query, variables));
            if (!response.ok)
                throw new Error(`HTTP error! Status: ${response.status}`);
            
            const data = await response.json();
            const opts = data.data.search.schools.edges.map(e => e.node);
            setOptions(opts);
        } catch (error) {
            console.error('Error fetching data:', error);
        } finally {
            setCanInteract(true);
        }
    };

    const chooseUniversity = (event) => {
        event.preventDefault();

        const index = event.target.id;
        const chosen = options[index];

        _setUniversity(chosen.name);
        setFinalizedOption(true);
        setUniversity(chosen.id);
        setOptions([]);
    }

    return (
        <div className = "general-container">
            <h1>
                {!finalizedOption && "Select University" || "University Name: " + _university}
            </h1>

            {!finalizedOption &&
                (<>
                    <div className = "text-input" id = "select-university">
                        <label htmlFor = "school">University Name:</label>

                        {canInteract &&
                            (<>
                                <input type = "text" onChange = {event => _setUniversity(event.target.value)} />
                                <button className = "btn" type = "submit" onClick = {getUniversities}>Search</button>
                            </>)
                        ||
                            (<>
                                <input type = "text" onChange = {event => _setUniversity(event.target.value)} disabled />
                                <button className = "btn" type = "submit" onClick = {getUniversities} disabled>Search</button>
                            </>)
                        }
                    </div>
                    
                    <div className = "choices" id = "univ-choices">
                        {
                            options.map((option, index) => (
                                <div key = {index} className = "text-input bubble">
                                    <input type = "radio" id = {index} value = {option.name} name = "univ-name" onClick = {chooseUniversity}></input>
                                    <label htmlFor = {option.name}>{option.name}</label>
                                </div>
                            ))
                        }
                    </div>
                </>)
            }
        </div>
    )
}

const SelectCourse = ({courses, add, deleteCourse, set, select, id, loaded, addProfs}) => {
    const [depts, setDepts] = useState([]);
    const [choseDept, setChoseDept] = useState(false);
    const [dept, setDept] = useState({});
    const [classes, setClasses] = useState([]);
    const [course, setCours] = useState("");
    const [profs, setProfs] = useState([]);
    const [showBtn, setShowBtn] = useState(false);
    const [pressed, setPressed] = useState(false);

    const setCourse = (event) => {
        setCours(event.target.value);
    };

    const setDepartment = (event) => {
        const index = event.target.options[event.target.selectedIndex].getAttribute('id');
        setDept(depts[index])
    }

    const addCourse = (event) => {
        event.preventDefault();
        add(course);
        setPressed(true);
    }

    const getDepts = async() => {
        const query = `query TeacherSearchResultsPageQuery( $query: TeacherSearchQuery! $schoolID: ID $includeSchoolFilter: Boolean! ) { search: newSearch { teachers(query: $query, first: 8, after: "") { filters { options { value id } } } } school: node(id: $schoolID) @include(if: $includeSchoolFilter) { __typename ... on School { name } id } } `;
        const variables = {query: {text: "", schoolID: id, fallback: true, departmentID: null}, includeSchoolFilter:true, schoolID: id};

        const response = await fetch (apiUrl, parameter(query, variables));
        const data = await response.json();
        let deptOptions = data.data.search.teachers.filters[0].options
        let departments = []

        for (let i = 0; i < deptOptions.length; i++){
            if (deptOptions[i].value != 'select department' || deptOptions[i].value != 'not specified'){
                departments.push({ id: deptOptions[i].id, value: capitalize(deptOptions[i].value) })
            }
        }
        setDepts(departments);
    }

   
    const getProfessorByDept = async(deptId) => {
        let professors = [];
        let hasNextPage = true;
        let cursor = null;
        const count = 8;
        while (hasNextPage) {
            const query = ` query TeacherSearchPaginationQuery($count: Int!, $cursor: String, $query: TeacherSearchQuery!) { search: newSearch { ...TeacherSearchPagination_search_1jWD3d } } fragment TeacherSearchPagination_search_1jWD3d on newSearch { teachers(query: $query, first: $count, after: $cursor) { didFallback edges { cursor node { ...TeacherCard_teacher id __typename } } pageInfo { hasNextPage endCursor } resultCount filters { field options { value id } } } } fragment TeacherCard_teacher on Teacher { id legacyId avgRating numRatings ...CardFeedback_teacher ...CardSchool_teacher ...CardName_teacher ...TeacherBookmark_teacher } fragment CardFeedback_teacher on Teacher { wouldTakeAgainPercent avgDifficulty } fragment CardSchool_teacher on Teacher { department school { name id } } fragment CardName_teacher on Teacher { firstName lastName } fragment TeacherBookmark_teacher on Teacher { id isSaved }`;
    
            const variables = {count: count, cursor: cursor, query: {text: '', schoolID: id, fallback: true, departmentID: deptId}};
            const response = await fetch(apiUrl, parameter(query, variables));
    
            const data = await response.json();
    
            if (data.errors) {
                throw new Error(`GraphQL query failed with errors: ${JSON.stringify(data.errors)}`);
            }
    
            const teachersData = data.data.search.teachers;
            professors = professors.concat(teachersData.edges.map(edge => edge.node.id));
            hasNextPage = teachersData.pageInfo.hasNextPage;
            cursor = hasNextPage ? teachersData.pageInfo.endCursor : null;
        }
        setProfs(professors);
    }

    const getCourses = async() => {
        const unique = new Set()
        const courses = [];
        const profByDept = [];
        for (let i = 0; i < profs.length; i++){
            let profId = profs[i];
            let tempCourse = [];
            const query = `query TeacherRatingsPageQuery($id: ID!) { node(id: $id) { __typename ... on Teacher { id firstName lastName departmentId courseCodes {courseName} } } } `; 
            const variables = {id:profId};
            const response = await fetch (apiUrl, parameter(query, variables));
            const data = await response.json();
            const courseCodes = data.data.node.courseCodes;
            courseCodes.forEach(course => {
                if (!unique.has(course.courseName)){
                    unique.add(course.courseName);
                    courses.push({'course' : course.courseName, 'dept': data.data.node.departmentId});
                    tempCourse.push(course.courseName);
                }
            })
        }

        setClasses(courses);
    };

    const getProfessorCourses = async () => {
        const unique = new Set()
        const profByDept = [];
        for (let i = 0; i < profs.length; i++){
            let profId = profs[i];
            let tempCourse = [];

            const query = `query TeacherRatingsPageQuery($id: ID!) { node(id: $id) { __typename ... on Teacher { id firstName lastName departmentId courseCodes {courseName} avgRating } } } `; 
            const variables = {id:profId};
            const response = await fetch (apiUrl, parameter(query, variables));
            const data = await response.json();
            const node = data.data.node;

            const courseCodes = node.courseCodes;
            courseCodes.forEach(course => {
            if (!unique.has(course.courseName)){
                    unique.add(course.courseName);
                    tempCourse.push(course.courseName);
                }
            })

            if (node && node.avgRating != 0 && pressed){
                let fullName = `${node.firstName} ${node.lastName}`
                let id = node.id
                let rating = node.avgRating
                let dept = node.departmentId
                profByDept.push({'name': fullName, 'id': id, 'rating':rating, 'courses': tempCourse, 'dept': dept});
                setPressed(false);
            }
        }

        addProfs(profByDept);
    }

    useEffect(() => {
        if (loaded){
            getDepts()
        }
      }, [loaded]);

      useEffect(() => {
        if (loaded){
            getProfessorByDept(dept.id)
        }
      }, [dept]);

      useEffect(() => {
        getCourses()
      }, [profs]);


      useEffect(() => {
        if (courses.length > 0){
            setShowBtn(true);
        }else{
            setShowBtn(false);
        }
        getProfessorCourses()
      }, [courses])

      return (
        <div className="general-container">
            <h1>Select Your Courses</h1>
                <div>
                     <div className="text-input">
                        <select id="dept1" name="depts" onChange={setDepartment} defaultValue = "" style={{borderRadius: '12px'}}>
                            <option value="" disabled>Department</option>
                            {depts.map((dept, index) => (
                            <option id={index} value={dept.value}>{dept.value}</option>
                            ))}
                        </select>
                        <select id="courses"  onChange={setCourse} defaultValue = "" style={{borderRadius: '12px'}}>
                            <option value="" disabled>Course</option>
                            {classes.map((clas, index) => (
                                <option data-dept = {clas} key={index} value={clas.course}>{clas.course}</option>
                            ))}
                        </select>

                        <button id="add-btn" onClick={addCourse}>+</button>
                    </div>

                    <div className="choices" style={{ margin: "2.5% 25% 0 25%" }}>
                        {courses.map((course, index) => (
                        <Course key={index} index={index} name={course} deleteFunction={() => deleteCourse(index)} />
                        ))}
                    </div>
                    {showBtn && (<button class="btn" id = "course-submit-btn" style={{marginTop: '2%', paddingTop: '6px', paddingBottom: '6px'}}>Submit</button>)}
                </div>
        </div>
    );
}
    
const Course = ({index, name, deleteFunction}) => {
    const onDelete = (event) =>{
        event.preventDefault();
        deleteFunction();
    }

    return (
    <div id = {`course${index}`} className = "bubble">
        <p style={{margin:"0px"}}>{name}</p>
        <button onClick={onDelete} className="remove-btn">x</button>
    </div>
    )
}

const SelectProfessor = ({ dataSet, courses }) => {

    useEffect(() => {
        console.log(courses);
    }, [courses]);

    return (
        <div className = "general-container">
            <h1>Select Your Professor</h1>
        </div>
    )
}

export default Form;