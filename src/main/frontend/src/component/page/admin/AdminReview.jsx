import React, {useState, useEffect} from 'react';
import {useLocation, useSearchParams, useNavigate} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {createPageAndSearchTypeKeyword} from "../../../modules/requestUrlModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import AdminReviewListForm from "./AdminReviewListForm";
import {mainProductPagingObject} from "../../../modules/pagingModule";

/*
    리뷰 목록 페이지
    useLocation을 통해
    /admin/review 인 경우 미처리 리뷰 목록
    /admin/review/all인 경우 전체 리뷰 목록 조회.

    여기는 미처리와 전체 select box 선택은 없고 sideNav 버튼에 따라 변경.

    검색은 사용자 아이디 또는 닉네임 기반 조회와 상품명 기반 조회.
    select box를 통해 선택해서 조회.
    상품명과 작성자로만 나오는데 작성자 기반 조회는 아이디 및 닉네임으로 조회 처리.
 */
function AdminReview() {
    const location = useLocation();
    const [params] = useSearchParams();
    const page = params.get('page');
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

    const navigate = useNavigate();

    useEffect(() => {
        setKeywordSelectValue(searchType);
        getReviewList();
    }, [location, page, keyword]);

    //리뷰 목록 조회
    const getReviewList = async () => {
        //useLocation 값에 따라 url Prefix 및 목록 header 정의
        const urlPrefix = location.pathname.substring(1);
        if(urlPrefix === 'admin/review')
            setContentHeader('미답변 리뷰');
        else
            setContentHeader('전체 리뷰');

        let url = `${urlPrefix}${createPageAndSearchTypeKeyword(page, keyword, searchType)}`;

        await axiosInstance.get(url)
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

    //검색 select box 이벤트
    const handleSelectOnChange = (e) => {
        const value = e.target.value;
        setKeywordSelectValue(value);
    }

    //검색 input 입력 이벤트
    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    //리뷰 목록 Element 클릭 이벤트
    //리뷰 상세페이지 이동
    const handleOnClick = (reviewId) => {
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