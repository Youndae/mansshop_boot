import React, {useEffect, useState} from 'react';

import { axiosInstance } from "../../../modules/customAxios";

import MainContent from "../../ui/MainContent";

/*
    메인 BEST 상품 목록 페이지. 애플리케이션의 index Component
    메인 페이지의 상품 목록들은 모두 MainContent Component를 통해 리스트 출력
 */
function Best() {
    const [data, setData] = useState([]);

    useEffect(() => {
        getBestData();
    }, []);

    //BEST 상품 조회
    const getBestData = async() => {

        await axiosInstance.get('main/')
            .then(res => {
                setData(res.data);
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