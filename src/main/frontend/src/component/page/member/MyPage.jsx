import React from 'react';
import axios from "axios";

function MyPage() {

    const handlePost = async () => {
        console.log('handlePost');
        await axios.post('/api/post', {}, {
            headers: {
                'Content-Type': 'application/json'
            },
            withCredentials: true,
        })
            .then(res => {
                console.log('post axios success : ', res);
            })
            .catch(err => {
                console.error('post axios error : ', err);
            })
    }

    return (
        <>
            <h1>마이페이지입니다</h1>
            <button type={'button'} onClick={handlePost}>post button</button>
        </>
    )
}

export default MyPage;