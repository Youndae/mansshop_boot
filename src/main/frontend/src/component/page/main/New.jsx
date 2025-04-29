import React, {useState, useEffect} from 'react';

import { axiosInstance } from "../../../modules/customAxios";

import MainContent from "../../ui/MainContent";

/*
    새로운 상품 리스트.
    상품 등록일을 기준으로 조회
 */
function New() {
    const [data, setData] = useState([]);

    useEffect(() => {
        getNewData();
    }, []);

    //상품 조회
    const getNewData = async() => {

        await axiosInstance.get(`main/new`)
            .then(res => {
                setData(res.data);
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