import React, {useEffect, useState} from 'react';
import {useNavigate, useParams, useSearchParams} from "react-router-dom";

import { axiosInstance } from "../../../modules/customAxios";
import {
    mainProductPagingObject,
    getClickNumber,
    getPrevNumber,
    getNextNumber,
    pageSubmit
} from "../../../modules/pagingModule";
import {createPageParam} from "../../../modules/requestUrlModule";

import MainContent from "../../ui/MainContent";
import Paging from "../../ui/Paging";

/*
    분류별 상품 목록 페이지
    선택한 상품 분류에 따라 해당하는 상품 목록 출력
 */
function MainClassification() {
    const { classification } = useParams();
    const [params] = useSearchParams();
    const page = params.get('page');

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

    //상품 목록 조회
    const getClassificationList = async() => {

        await axiosInstance.get(`main/${classification}${createPageParam(page)}`)
            .then(res => {
                setData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });

            })
    }

    //페이지네이션 버튼 이벤트
    const handlePageBtn = (e) => {
        pageSubmit(getClickNumber(e), navigate);
    }

    //페이지네이션 이전 버튼 이벤트
    const handlePagePrev = () => {
        pageSubmit(getPrevNumber(pagingData), navigate);
    }

    //페이지네이션 다음 버튼 이벤트
    const handlePageNext = () => {
        pageSubmit(getNextNumber(pagingData));
    }

    return (
        <>
            <MainContent
                data={data}
                classification={classification}
            />
            <Paging
                pagingData={pagingData}
                onClickNumber={handlePageBtn}
                onClickPrev={handlePagePrev}
                onClickNext={handlePageNext}
                className={null}
            />
        </>
    )
}

export default MainClassification;