import {useState} from "react";
import "../styles/Form.css"

const Form = () => {
    const [select, setSelect] = useState("");
    const [courses, setCourses] = useState([]);

    const setCourse = (event) => {
        event.preventDefault();
        setSelect(event.target.value);
    };

    const addCourse = (event) => {
        event.preventDefault();
        if (select) {
            setCourses([...courses, select]); //research
        }
    };

    const deleteCourse = (index) =>{
        const updatedCourses = courses.filter((course, i) => i !== index); //research
        setCourses(updatedCourses);
    }

    return (
        <div id="form-container">
        <form id = "f">
            <div className="general-container">
                <h1>Select University</h1>

                <div className = "text-input">
                    <label htmlFor = "school">University Name:</label>
                    <input type = "text" style={{borderRadius: "10px", border: "solid 1px"}}></input>
                </div>
            </div>
         

            <div className = "general-container">
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

            
        </form>
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



export default Form;