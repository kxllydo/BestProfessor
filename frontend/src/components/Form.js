import {useState, useEffect} from "react";
import "../styles/Form.css"

const Form = () => {
    const [select, setSelect] = useState("");
    const [courses, setCourses] = useState([]);
    const [choseUniv, setChoseUniv] = useState(false);
    const [univ, setUniv] = useState({});
    const [univId, setUnivId] = useState("");

    const setCourse = (event) => {
        setSelect(event.target.value);
    };

    const addCourse = () => {
        if (select) {
            setCourses([...courses, select]); //research
        }
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

    return (
        <div id="form-container">
            <SelectUniversity handleUniversity={setUniversity}/>
            
            {choseUniv && (
            <SelectCourse courses = {courses} add = {addCourse} deleteCourse={deleteCourse} set = {setCourse} select = {select} id = {univId}/>)
            }
            
        </div>
    );
}

const SelectUniversity = ({handleUniversity}) => {
    const [univ, setUniv] = useState("");
    const [nameOptions, setNameOptions] = useState([]);
    const [options, setOptions] = useState([]);
    
    const setUniversity = (event) => {
        event.preventDefault();
        setUniv(event.target.value);
    }

    const getUniversities = async (event) => {
        event.preventDefault();
        const div = document.getElementById("univ-choices");
        if (div.style.display == "none"){
            div.style.display = "flex";
        }

        console.log(univ);
        const response = await fetch (`http://localhost:8080/api/university-options/${univ}`,
            {
                method: "GET",
            }
        );
        let choices = [];
        const data = await response.json();
        const opt = data.options;
        setOptions(opt);

        for (let i = 0; i < opt.length; i++){
            choices.push(opt[i].name);
        }
        setNameOptions(choices);
    }

    const chooseUniversity = (event) => {
        event.preventDefault();
        const index = event.target.id;
        const chosen = options[index];
      
        handleUniversity({chosen});
        const div = document.getElementById("univ-choices");
        div.style.display = "none";
        const form = document.getElementById("select-university");
        form.innerHTML = `University Name : ${chosen.name}`;

        setNameOptions([]);
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
                    nameOptions.map((option, index) => (
                        <div className="text-input bubble" style={{gap: "2px", paddingLeft: "8px"}}>
                        <input type ="radio" id = {index} value = {option} name = "univ-name" onClick={chooseUniversity}></input>
                        <label htmlFor={option}>{option}</label>
                        </div>
                    ))
                }
            </div>
        </div>
    )
}

const SelectCourse = ({courses, add, deleteCourse, set, select, id}) => {
    const setCourse = (event) => {
        event.preventDefault();
        set(event);
    };

    const addCourse = (event) => {
        event.preventDefault();
        add();
    }

    return (
<       div className = "general-container">
            <h1>Select Your Courses</h1>
            <div className = "text-input">
            <select id = "dept1" name = "depts">
                <option value="" disabled>Department</option>
                <option value ="math">math</option>
            </select>

            <select id = "course1" name = "courses"  value = {select} onChange={setCourse}>
                <option value="" disabled>Course</option>
                <option value ="CS250">CS250</option>
                <option value ="CS270">me</option>
                <option value ="CS290">bruh</option>
            </select>

            <button id ="add-btn" onClick={addCourse}>+</button>

            </div>

            <div className = "choices" style={{margin: "2.5% 25% 0 25%"}}>
            {courses.map((course, index) => (
                <Course index = {index} name={course} deleteFunction={() => deleteCourse(index)} />
            ))}
            </div>
        </div>
    )
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



export default Form;