import React, {useEffect, useState} from 'react';
import {useNavigate, useParams, useSearchParams} from "react-router-dom";
import {useDispatch, useSelector} from "react-redux";

import { axiosInstance } from "../../../modules/customAxios";
import {
    mainProductPagingObject,
    getClickNumber,
    getPrevNumber,
    getNextNumber,
    pageSubmit
} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";

import MainContent from "../../ui/MainContent";
import Paging from "../../ui/Paging";

function MainClassification() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
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

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getClassificationList();
    }, [page, classification]);

    const getClassificationList = async() => {
        await axiosInstance.get(`/main/${classification}?page=${page}`)
            .then(res => {
                setData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: page,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);

            })
    }

    const handlePageBtn = (e) => {
        pageSubmit(getClickNumber(e), navigate);
    }

    const handlePagePrev = () => {
        pageSubmit(getPrevNumber(pagingData), navigate);
    }

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