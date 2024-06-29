import React, {useEffect, useState} from 'react';

import { axiosInstance } from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    mainProductPagingObject,
    pageSubmit, searchPageSubmit, searchSubmit
} from "../../../modules/pagingModule";

import {useNavigate, useSearchParams} from "react-router-dom";
import Paging from "../../ui/Paging";
import MainContent from "../../ui/MainContent";

function SearchProduct() {
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
    const keyword = params.get('keyword');
    console.log('search : ', keyword);

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

    const getSearchProductList = async () => {
        await axiosInstance.get(`/main/search?keyword=${keyword}&page=${page}`)
            .then(res => {
                setData(res.data.content);
                console.log('search axios res : ', res);
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
                console.error('search axios error : ', err);
            })
    }

    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    const handlePagingSubmit = (pageNum) => {
        searchPageSubmit(keyword, pageNum, navigate);
        /*if(keyword == null)
            navigate(`/admin/product/discount?page=${pageNum}`);
        else
            navigate(`/admin/product/discount?keyword=${keyword}&page=${pageNum}`);*/
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