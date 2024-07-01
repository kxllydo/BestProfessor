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
            'Accept': '*/*',
            'Accept-Language': 'en-US,en;q=0.9',
            'Cookie': 'cid=5WKtlOpq2c-20240604; _ga=GA1.1.246269703.1717526946; ccpa-notice-viewed-02=true; oauthState=Y49VeQCF8mobIFwpskRUquszPwti0IPq4ZnttsTLvmI; oauthProvider=google; userSchoolId=U2Nob29sLTE1MjE=; userSchoolLegacyId=1521; userSchoolName=Drexel%20University; _ga_WET17VWCJ3=GS1.1.1719431599.29.1.1719432629.0.0.0',
            'Dnt': '1',
            'Host': 'www.ratemyprofessors.com',
            'Origin': 'https://www.ratemyprofessors.com',
            'Referer': 'https://www.ratemyprofessors.com/search/professors/1521?q=*',
            'Sec-Ch-Ua': '"Not/A)Brand";v="8", "Chromium";v="126", "Microsoft Edge";v="126"',
            'Sec-Ch-Ua-Mobile': '?0',
            'Sec-Ch-Ua-Platform': '"Windows"',
            'Sec-Fetch-Dest': 'empty',
            'Sec-Fetch-Mode': 'cors',
            'Sec-Fetch-Site': 'same-origin'
        },
        body: JSON.stringify(payload),
    };
};

const apiUrl = 'https://www.ratemyprofessors.com/graphql';

const Form = () => {
    const [select, setSelect] = useState("");
    const [courses, setCourses] = useState([]);
    const [choseUniv, setChoseUniv] = useState(false);
    const [univ, setUniv] = useState({});
    const [univId, setUnivId] = useState("");

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
    }

    useEffect(() => {
        console.log(courses);
    }, [courses])

    return (
        <div id="form-container">
            <SelectUniversity handleUniversity={setUniversity}/>
            
            {/* {choseUniv && ( */}
            <SelectCourse courses = {courses} add = {addCourse} deleteCourse={deleteCourse} set = {setCourse} select = {select} id = {univId}/>
            {/* )
            } */}
            
        </div>
    );
}


const SelectUniversity = ({handleUniversity}) => {
    const [univ, setUniv] = useState("");
    const [options, setOptions] = useState([]);
    ;
    const setUniversity = (event) => {
        event.preventDefault();
        setUniv(event.target.value);
    }

    const getUniversities = async(event) => {
        event.preventDefault();
        const div = document.getElementById("univ-choices");
        if (div.style.display == "none"){
            div.style.display = "flex";
        };

        const query = "query SchoolSearchResultsPageQuery(\n  $query: SchoolSearchQuery!\n) {\n  search: newSearch {\n    ...SchoolSearchPagination_search_1ZLmLD\n  }\n}\n\nfragment SchoolSearchPagination_search_1ZLmLD on newSearch {\n  schools(query: $query, first: 8, after: \"\") {\n    edges {\n      cursor\n      node {\n        name\n        ...SchoolCard_school\n        id\n        __typename\n      }\n    }\n    pageInfo {\n      hasNextPage\n      endCursor\n    }\n    resultCount\n  }\n}\n\nfragment SchoolCard_school on School {\n  legacyId\n  name\n  numRatings\n  avgRating\n  avgRatingRounded\n  ...SchoolCardHeader_school\n  ...SchoolCardLocation_school\n}\n\nfragment SchoolCardHeader_school on School {\n  name\n}\n\nfragment SchoolCardLocation_school on School {\n  city\n  state\n}\n";

        const variables = {
            query: {
              text: univ
            }
        };

        try {
            const response = await fetch(apiUrl, parameter('Basic dGVzdDp0ZXN0', query, variables));
            if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
            }
            const data = await response.json();
            const opts = data.data.search.schools.edges.map(e => e.node);
            console.log(opts);
            setOptions(opts);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };
          

    const chooseUniversity = (event) => {
        event.preventDefault();
        const index = event.target.id;
        const chosen = options[index];
        console.log(chosen);
      
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
                <input type = "text" style={{borderRadius: "10px", border: "solid 1px"}} onChange={setUniversity}></input>
                <button type="submit" onClick={getUniversities}>Search</button>
            </div>

            <div className ="choices" style={{margin: "3% 0 0 0 "}} id = "univ-choices">
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

const SelectCourse = ({courses, add, deleteCourse, set, select, id}) => {
    const [depts, setDepts] = useState([]);
    const [choseDept, setChoseDept] = useState(false);
    const [dept, setDept] = useState("");
    const [classes, setClasses] = useState([]);
    const [course, setCours] = useState("");
    const [loaded, setLoaded] = useState(false);

    const setCourse = (event) => {
        setCours(event.target.value);

    };

    const setDepartment = (event) => {
        setDept(event.target.value);
    }

    const addCourse = (event) => {
        event.preventDefault();
        console.log(course);

        if (course)
            add(course);
    }

    const getDepts = async() => {
        const response = await fetch (`api/departments/${id}`, 
            {
                method: "GET",
            }
        );
        const data = await response.json();
        setDepts(data);
        setLoaded(true)
    }

    const getCourses = async() => {
        console.log(dept);
        const response = await fetch (`/api/courses/${id}/${dept}`, 
            {
                method : "GET",
            }
        )

        const data = await response.json();
        setClasses(data);
        setChoseDept(true);
    }

    useEffect(() => {
        if (dept)
            getCourses();
    }, [dept])


    useEffect(() => {
        getDepts()
      }, []);


      return (
        <div className="general-container">
            <h1>Select Your Courses</h1>
            {loaded && (
                <div>
                    {/* <div className="text-input">
                        <select id="dept1" name="depts" onChange={setDepartment} defaultValue = "">
                            <option value="" disabled>Department</option>
                            {depts.map((dept, index) => (
                                <option key={index} value={dept}>{dept}</option>
                            ))}
                        </select>
                        <select id="course1" name="courses" onChange={setCourse} defaultValue = "">
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
                    </div> */}
                </div>
            )}
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



export default Form;

// "query SchoolSearchResultsPageQuery(  $query: SchoolSearchQuery!\n) {\n  search: newSearch {\n    ...SchoolSearchPagination_search_1ZLmLD\n  }\n}\n\nfragment SchoolSearchPagination_search_1ZLmLD on newSearch {\n  schools(query: $query, first: 8, after: \"\") {\n    edges {\n      cursor\n      node {\n        name\n        ...SchoolCard_school\n        id\n        __typename\n      }\n    }\n    pageInfo {\n      hasNextPage\n      endCursor\n    }\n    resultCount\n  }\n}\n\nfragment SchoolCard_school on School {\n  legacyId\n  name\n  numRatings\n  avgRating\n  avgRatingRounded\n  ...SchoolCardHeader_school\n  ...SchoolCardLocation_school\n}\n\nfragment SchoolCardHeader_school on School {\n  name\n}\n\nfragment SchoolCardLocation_school on School {\n  city\n  state\n}\n"

