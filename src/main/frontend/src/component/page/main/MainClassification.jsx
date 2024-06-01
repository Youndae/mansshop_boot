import React, {useEffect, useState} from 'react';

import { defaultAxios } from "../../../module/customAxios";
import { mainProductPagingObject, getClickNumber, getPrevNumber, getNextNumber } from "../../../module/pagingModule";

import MainContent from "../../ui/MainContent";
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
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

    const navigate = useNavigate();

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

    const handleOnClickNumber = (e) => {
        paginationNavigate(getClickNumber(e));
    }

    const handleOnClickPrev = () => {
        paginationNavigate(getPrevNumber(pagingData));
    }

    const handleOnClickNext = () => {
        paginationNavigate(getNextNumber(pagingData));
    }

    const paginationNavigate = (clickNo) => {
        navigate(`?page=${clickNo}`);
    }

    return (
        <>
            <MainContent
                data={data}
                classification={classification}
            />
            <Paging
                keyword={null}
                pagingData={pagingData}
                onClickNumber={handleOnClickNumber}
                onClickPrev={handleOnClickPrev}
                onClickNext={handleOnClickNext}
                className={null}
            />
        </>
    )
}

export default MainClassification;