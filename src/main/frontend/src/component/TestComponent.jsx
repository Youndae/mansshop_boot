import React, {useEffect, useState} from 'react';
import axios from "axios";
import { useCookies } from "react-cookie";
import {Link} from "react-router-dom";

function TestComponent() {
    const [printText, setPrintText] = useState();
    const [cookies] = useCookies(['XSRF-TOKEN']);

    useEffect(() => {
        getPrintText();
    }, []);

    const getPrintText = async () => {

        await axios.get('/api/main', {
            headers: {'Content-Type' : 'application/json'},
            withCredentials: true,
        })
            .then(res => {
                console.log('test Text res : ', res);
                setPrintText(res.data);
            })
            .catch(err => {
                console.error(err);
            })
    }

    const handleSubmit = async () => {
        await axios.post(`/api/`, {}, {
            headers: {
                'Content-Type': 'application/json'
            },
            withCredentials: true,
        })
            .then(res => {
                console.log('posts success');
            })
            .catch(err => {
                console.error('posts fail : ', err);
            })
    }



    return (
        <div>
            return : {printText}
            <div>
                <button type={'button'} onClick={handleSubmit}>button</button>
                <Link to={'/login'}>login</Link>
            </div>
        </div>
    )
}

export default TestComponent;