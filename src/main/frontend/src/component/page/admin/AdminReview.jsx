import React, {useState, useEffect} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useLocation, useSearchParams, useNavigate} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import AdminReviewListForm from "./AdminReviewListForm";
import {productDetailPagingObject} from "../../../modules/pagingModule";

function AdminReview() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const location = useLocation();
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
    const keyword = params.get('keyword');
    const searchType = params.get('type') == null ? 'user' : params.get('type');

    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        totalElements: 0,
        activeNo: page,
    });
    const [keywordInput, setKeywordInput] = useState('');
    const [keywordSelectValue, setKeywordSelectValue] = useState('');
    const [contentHeader, setContentHeader] = useState('');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        setKeywordSelectValue(searchType);
        getReviewList();
    }, [location, page, keyword]);

    const getReviewList = async () => {
        const urlPrefix = location.pathname.substring(1);
        if(urlPrefix === 'admin/review')
            setContentHeader('미답변 리뷰');
        else
            setContentHeader('전체 리뷰');

        let url = `${urlPrefix}?page=${page}`;
        if(keyword !== null)
            url += `&keyword=${keyword}&searchType=${searchType}`;

        await axiosInstance.get(url)
            .then(res => {
                console.log('reviewList res : ', res);
                setData(res.data.content);

                const pagingObject = productDetailPagingObject(page, res.data.totalPages);
                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    totalElements: res.data.totalElements,
                    activeNo: page,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleSelectOnChange = (e) => {
        const value = e.target.value;
        setKeywordSelectValue(value);
    }

    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    const handleOnClick = (reviewId) => {
        console.log('review list handleOnClick');
        navigate(`/admin/review/detail/${reviewId}`);
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'review'}
            />
            <AdminReviewListForm
                header={contentHeader}
                data={data}
                handleOnClick={handleOnClick}
                keywordSelectValue={keywordSelectValue}
                handleSelectOnChange={handleSelectOnChange}
                handleKeywordOnChange={handleKeywordOnChange}
                keywordInput={keywordInput}
                pagingData={pagingData}
                keyword={keyword}
            />
        </div>
    )

}

export default AdminReview;