import React, {useEffect, useState} from 'react';

import {axiosDefault, defaultAxios} from "../../../modules/customAxios";

import MainContent from "../../ui/MainContent";

function Best() {
    const [data, setData] = useState([]);

    useEffect(() => {
        getBestData();
    }, []);

    const getBestData = async() => {
        await axiosDefault.get('main/')
            .then(res => {
                console.log('best res : ', res);
                setData(res.data.content);
            })
            .catch(err => {
                console.log('best page error : ', err);
            })
    }

    return (
        <>
            <MainContent
                data={data}
                classification={'BEST'}
            />
        </>
    )
}

export default Best;