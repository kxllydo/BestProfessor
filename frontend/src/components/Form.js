import {useState} from "react";
import "../styles/Form.css"

const Form = () => {
    const [select, setSelect] = useState("");
    const [courses, setCourses] = useState([]);
    const [choseUniv, setChoseUniv] = useState(false);
    const [univ, setUniv] = useState("");

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

    const getUniversities = async (event) => {
        event.preventDefault();
        console.log(univ);
        const response = await fetch (`http://localhost:8080/api/university-options/drexel`,
            {
                method: "GET",
            }
        );

        const data = await response.json();
        console.log(data);
    }

    const setUniversity = (event) => {
        event.preventDefault();
        setUniv(event.target.value);
    }

    return (
        <div id="form-container">
        <form id = "university">
            <div className="general-container">
                <h1>Select University</h1>

                <div className = "text-input">
                    <label htmlFor = "school">University Name:</label>
                    <input type = "text" style={{borderRadius: "10px", border: "solid 1px"}} onChange={setUniversity}></input>
                    <button type="submit" onClick={getUniversities}>Submit</button>
                </div>
            </div>
        
            </form>

            {choseUniv && (
            <SelectCourse courses = {courses} add = {addCourse} deleteCourse={deleteCourse} set = {setCourse} select = {select}/>)
            }
            
        </div>
    );
}

const SelectCourse = ({courses, add, deleteCourse, set, select}) => {

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
                <option value="" disabled selected>Course</option>
                <option value ="CS250">CS250</option>
                <option value ="CS270">me</option>
                <option value ="CS290">bruh</option>
            </select>

            <button id ="add-btn" onClick={addCourse}>+</button>

            </div>

            <div id = "courses">
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