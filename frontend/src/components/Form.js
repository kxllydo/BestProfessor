import {useState, useEffect} from "react";

import "../styles/Form.css"

const parameter = (token, query, variables) => {
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

const apiUrl = 'https://www.ratemyprofessors.com/graphql';

const Form = () => {
    const [select, setSelect] = useState("");
    const [courses, setCourses] = useState([]);
    const [choseUniv, setChoseUniv] = useState(false);
    const [univ, setUniv] = useState({});
    const [univId, setUnivId] = useState("");
    const [loaded, setLoaded] = useState(false);

    const [showCourses, setShowCourses] = useState(false);


    const setCourse = (event) => {
        setSelect(event.target.value);
    };

    const addCourse = (course) => {
        setCourses((prevCourses) => [...prevCourses, course]);
    };

    const deleteCourse = (index) =>{
        const updatedCourses = courses.filter((course, i) => i !== index); //research
        setCourses(updatedCourses);
    }

    const setUniversity = ({chosen}) => {
        setUniv({chosen});
        setChoseUniv(true);
        setUnivId(chosen.id);
        setLoaded(true);
    }

    return (
        <div id="form-container">
            <SelectUniversity handleUniversity={setUniversity}/>
            
            { choseUniv &&
                <SelectCourse 
                    courses = {courses} 
                    add = {addCourse} 
                    deleteCourse = {deleteCourse} 
                    set = {setCourse} 
                    select = {select} 
                    id = {univId}
                    loaded = {loaded}
                    />
            }

            { choseUniv && courses.length > 0 &&
                <SelectProfessor courses = {courses} />
            }
        </div>
    );
}


const SelectUniversity = ({handleUniversity}) => {
    const [univ, setUniv] = useState("");
    const [options, setOptions] = useState([]);

    const getUniversities = async(event) => {
        event.preventDefault();

        const query = `query SchoolSearchResultsPageQuery($query: SchoolSearchQuery!) { search: newSearch { schools(query: $query) { edges { node { id name } } } } } `;
        const variables = {query: {text: univ}};

        try {
            const response = await fetch(apiUrl, parameter('Basic dGVzdDp0ZXN0', query, variables));
            if (!response.ok)
                throw new Error(`HTTP error! Status: ${response.status}`);
            
            const data = await response.json();
            const opts = data.data.search.schools.edges.map(e => e.node);
            setOptions(opts);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };
          

    const chooseUniversity = (event) => {
        event.preventDefault();
        const index = event.target.id;
        const chosen = options[index];
      
        handleUniversity({chosen});
        const div = document.getElementById("univ-choices");
        div.style.display = "none";
        const form = document.getElementById("select-university");
        form.innerHTML = `University Name : ${chosen.name}`;

        setOptions([]);
    }

    return (
        <div className="general-container">
            <h1>Select University</h1>

            <div className = "text-input" id = "select-university">
                <label htmlFor = "school">University Name:</label>
                <input type = "text" onChange = {event => setUniv(event.target.value)}></input>
                <button type = "submit" className="btn" onClick = {getUniversities}>Search</button>
            </div>

            <div className ="choices" id = "univ-choices">
                {
                    options.map((option, index) => (
                        <div className="text-input bubble" style={{gap: "2px", paddingLeft: "8px"}}>
                        <input type ="radio" id = {index} value = {option.name} name = "univ-name" onClick={chooseUniversity}></input>
                        <label htmlFor={option.name}>{option.name}</label>
                        </div>
                    ))
                }
            </div>
        </div>
    )
}

const SelectCourse = ({courses, add, deleteCourse, set, select, id, loaded}) => {
    const [depts, setDepts] = useState([]);
    const [choseDept, setChoseDept] = useState(false);
    const [dept, setDept] = useState({});
    const [classes, setClasses] = useState([]);
    const [course, setCours] = useState("");
    const [profs, setProfs] = useState([]);

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
    }

    const getDepts = async() => {
        const query = `query TeacherSearchResultsPageQuery( $query: TeacherSearchQuery! $schoolID: ID $includeSchoolFilter: Boolean! ) { search: newSearch { teachers(query: $query, first: 8, after: "") { filters { options { value id } } } } school: node(id: $schoolID) @include(if: $includeSchoolFilter) { __typename ... on School { name } id } } `;
        const variables = {query: {text: "", schoolID: id, fallback: true, departmentID: null}, includeSchoolFilter:true, schoolID: id};

        const response = await fetch (apiUrl, parameter("Basic dGVzdDp0ZXN0", query, variables));
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
            const response = await fetch(apiUrl, parameter('Basic dGVzdDp0ZXN0', query, variables));
    
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
        for (let i = 0; i < profs.length; i++){
            let profId = profs[i];
            const query = `query TeacherRatingsPageQuery($id: ID!) { node(id: $id) { __typename ... on Teacher { id firstName lastName department courseCodes {courseName}  } } } `; 
            const variables = {id:profId};
            const response = await fetch (apiUrl, parameter("Basic dGVzdDp0ZXN0", query, variables));
            const data = await response.json();
            const courseCodes = data.data.node.courseCodes;
            courseCodes.forEach(course => {
                if (!unique.has(course.courseName)){
                    unique.add(course.courseName);
                    courses.push(course.courseName);
                }
            })
        }
        setClasses(courses);
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
            return (
                <button class="btn" id = "course-submit-btn" style={{marginTop: '2%'}}>Submit</button>
            )
        }
      }, [courses])

      return (
        <div className="general-container">
            <h1>Select Your Courses</h1>
                <div>
                     <div className="text-input">
                        <select id="dept1" name="depts" onChange={setDepartment} defaultValue = "">
                            <option value="" disabled>Department</option>
                            {depts.map((dept, index) => (
                                <option id={index} value={dept.value}>{dept.value}</option>
                            ))}
                        </select>
                        <select id="courses"  onChange={setCourse} defaultValue = "">
                            <option value="" disabled>Course</option>
                            {classes.map((clas, index) => (
                                <option key={index} value={clas}>{clas}</option>
                            ))}
                        </select>
                        <button id="add-btn" onClick={addCourse}>+</button>
                    </div>
                    <div className="choices" style={{ margin: "2.5% 25% 0 25%" }}>
                        {courses.map((course, index) => (
                            <Course key={index} index={index} name={course} deleteFunction={() => deleteCourse(index)} />
                        ))}
                    </div>
                </div>
        </div>
    );
};
    
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

const SelectProfessor = ({ courses }) => {
    const [a, b] = useState(3);

    useEffect(() => {
        b(a + 1);
    }, [courses])

    return (
       <div className = "general-container">
            <h1>Select Your Professors</h1>

            <h1> {a} </h1>
       </div>
    )
}


export default Form;
