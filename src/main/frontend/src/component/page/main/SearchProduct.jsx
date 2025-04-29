import React, {useEffect, useState} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";

import { axiosInstance } from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    mainProductPagingObject,
    searchPageSubmit
} from "../../../modules/pagingModule";
import {createPageAndKeywordUrl} from "../../../modules/requestUrlModule";

import Paging from "../../ui/Paging";
import MainContent from "../../ui/MainContent";

/*
    상품 검색 컴포넌트
 */
function SearchProduct() {
    const [params] = useSearchParams();
    const page = params.get('page');
    const keyword = params.get('keyword');

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
        getSearchProductList();
    }, [page, keyword]);

    //상품 목록 조회
    const getSearchProductList = async () => {
        await axiosInstance.get(`/main/search${createPageAndKeywordUrl(page, keyword)}`)
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
        handlePagingSubmit(getClickNumber(e));
    }

    //페이지네이션 이전 버튼 이벤트
    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    //페이지네이션 다음 버튼 이벤트
    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    //페이지네이션 제어
    const handlePagingSubmit = (pageNum) => {
        searchPageSubmit(keyword, pageNum, navigate);
    }

    return (
        <>
            <MainContent
                data={data}
                classification={''}
            />
            <Paging
                pagingData={pagingData}
                onClickNumber={handlePageBtn}
                onClickPrev={handlePagePrev}
                onClickNext={handlePageNext}
            />
        </>
    )
}

export default SearchProduct;