import React, {useEffect, useState} from 'react';

import { defaultAxios } from "../../../module/customAxios";
import { mainProductPagingObject } from "../../../module/pagingModule";

import MainContent from "../../ui/MainContent";
import {useParams, useSearchParams} from "react-router-dom";
import Paging from "../../ui/Paging";

function MainClassification() {
    const { classification } = useParams();
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');

    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });

    useEffect(() => {
        getClassificationList();
    }, [page, classification]);

    const getClassificationList = async() => {
        await defaultAxios.get(`/main/${classification}?page=${page}`)
            .then(res => {
                console.log('Main classification res : ', res);
                setData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: page,
                });
            })
            .catch(err => {
                console.error('classification error : ', err);
            })
    }

    return (
        <>
            <MainContent
                data={data}
                classification={classification}
            />
            <Paging
                pagingData={pagingData}
                keyword={null}
            />
        </>
    )
}

export default MainClassification;