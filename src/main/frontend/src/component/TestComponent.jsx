import React, {useEffect, useState} from 'react';
import axios from "axios";

function TestComponent() {
    const [printText, setPrintText] = useState();

    useEffect(() => {
        getPrintText();
    }, []);

    const getPrintText = async () => {
        await axios.get('/api/main', {
            headers: {'Content-Type' : 'application/json'},
            withCredentials: true,
        })
            .then(res => {
                console.log('test Text res : ', res.data);
                setPrintText(res.data);
            })
            .catch(err => {
                console.error(err);
            })
    }

    return (
        <div>
            return : {printText}
        </div>
    )
}

export default TestComponent;