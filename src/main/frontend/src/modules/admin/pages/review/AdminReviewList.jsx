import React, {useState, useEffect} from 'react';
import {useLocation, useSearchParams, useNavigate} from "react-router-dom";

import { 
	getNewReviewList, 
	getAllReviewList 
} from '../../services/adminReviewService';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';

import AdminSideNav from '../../components/AdminSideNav';
import AdminReviewListForm from '../../components/AdminReviewListForm';
import { buildQueryString } from '../../../../common/utils/queryStringUtils';

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
function AdminReviewList() {
	const location = useLocation();
    const [params] = useSearchParams();
	const { page, keyword, searchType = 'user' } = Object.fromEntries([...params]);

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

	const setResponseData = (res) => {
		setData(res.data.content);

		const pagingObject = mainProductPagingObject(page, res.data.totalPages);

		setPagingData({
			startPage: pagingObject.startPage,
			endPage: pagingObject.endPage,
			prev: pagingObject.prev,
			next: pagingObject.next,
			activeNo: pagingObject.activeNo,
		});
	}

	useEffect(() => {
		const getNewList = async () => {
			try {
				const res = await getNewReviewList(page, keyword, searchType);
	
				setResponseData(res);
			} catch(err) {
				console.log(err);
			}
		}
	
		const getAllList = async () => {
			try {
				const res = await getAllReviewList(page, keyword, searchType);
	
				setResponseData(res);
			} catch(err) {
				console.log(err);
			}
		}

		window.scrollTo(0, 0);
		setKeywordSelectValue(searchType);

		//useLocation 값에 따라 url Prefix 및 목록 header 정의
        const urlPrefix = location.pathname.substring(1);
        if(urlPrefix === 'admin/review'){
            setContentHeader('미답변 리뷰');
			getNewList();
		}else{
            setContentHeader('전체 리뷰');
			getAllList();
		}
	}, [location, page, keyword]);
	
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

	const handlePageBtn = (page) => {
		handlePageChange({
			typeOrNumber: page,
			pagingData,
			navigate,
			keyword: keywordInput,
			searchType: keywordSelectValue,
		})
	}

	const handleSearchOnClick = () => {
		const queryString = buildQueryString({
			keyword: keywordInput,
			searchType: keywordSelectValue,
		});

		navigate(`${queryString}`);
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
				handlePageBtn={handlePageBtn}
				handleSearchOnClick={handleSearchOnClick}
            />
        </div>
    )
}

export default AdminReviewList;