import React, {useState, useEffect} from 'react';

import { defaultAxios } from "../../../modules/customAxios";

import MainContent from "../../ui/MainContent";

function New() {
    const [data, setData] = useState([]);

    useEffect(() => {
        getNewData();
    }, []);

    const getNewData = async() => {

        await defaultAxios.get(`main/new`)
            .then(res => {
                setData(res.data.content);
            })
            .catch(err => {
                console.log('new page error : ', err);
            })
    }

    return (
        <>
            <MainContent
                data={data}
                classification={'NEW'}
            />
        </>
    )
}

export default New;