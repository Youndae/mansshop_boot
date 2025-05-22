import React, {useState, useEffect} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";

import { getProductQnAList } from '../../services/adminQnAService';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';
import { buildQueryString } from '../../../../common/utils/queryStringUtils';

import AdminSideNav from '../../components/AdminSideNav';
import AdminQnAListForm from '../../components/AdminQnAListForm';

/*
	상품 문의 목록.

	테이블 구조로 출력.
	테이블 오른쪽 상단의 select box로 미답변, 전체 선택을 통해 목록 제어.
	클릭 시 상세 페이지 이동

	상품 분류, 상품명, 작성자, 작성일, 답변 상태 구조로 출력.

	테이블 오른쪽 상단에 select box로 미답변 문의만 보기, 전체 문의 보기를 만들어준다.

	클릭 시 상세 페이지로 이동하도록 처리한다.

	검색은 사용자 아이디 또는 닉네임 기반 검색.
	검색 타입은 따로 없고 입력값을 그대로 닉네임과 아이디에서 검색
*/
function AdminProductQnAList() {
	const [params] = useSearchParams();
	const { page, keyword} = Object.fromEntries(params);
	const type = params.get('type')?.trim() || 'new';

	const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [typeSelectData, setTypeSelectData] = useState(type);
    const [keywordInput, setKeywordInput] = useState('');
    const [thText, setThText] = useState([]);

    const navigate = useNavigate();

	const getQnAList = async (page, keyword, type) => {
		try {
			const res = await getProductQnAList(page, keyword, type);

			setData(res.data.content);

			const pagingObject = mainProductPagingObject(page, res.data.totalPages);

			setPagingData({
				startPage: pagingObject.startPage,
				endPage: pagingObject.endPage,
				prev: pagingObject.prev,
				next: pagingObject.next,
				activeNo: pagingObject.activeNo,
			});
		} catch(err) {
			console.log(err);
		}
	}

	useEffect(() => {
		//리스트 th 정의
		//List Component를 MemberQnA와 같이 사용하므로 정의 필요.
		const thTextSet = () => {
			const textArr = [];

			textArr.push('상품분류');
			textArr.push('상품명');
			textArr.push('작성자');
			textArr.push('작성일');
			textArr.push('답변 상태');

			setThText(textArr);
		}

		window.scrollTo(0, 0);
		setTypeSelectData(type);
		thTextSet();
		getQnAList(page, keyword, type);
	}, [page, keyword, type]);

	//검색 input 입력 이벤트
    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

	//리스트 타입 ( 미처리, 전체 ) select box 이벤트
    const handleSelectOnChange = (e) => {
        const value = e.target.value;

		navigate(`/admin/qna/product?type=${value}`);
    }

    //상세 페이지 이동 이벤트
    const handleOnClick = (qnaId) => {
        navigate(`/admin/qna/product/${qnaId}`);
    }

	const handlePageBtn = (typeOrNumber) => {
		handlePageChange({
			typeOrNumber,
			pagingData,
			navigate,
			listType: typeSelectData,
			keyword,
		});
	}

	const handleSearchBtn = () => {
		if(keywordInput !== '') {
			const queryString = buildQueryString({
				keyword: keywordInput,
				type: 'all',
			});

			navigate(`${queryString}`);
		}else
			alert('검색어를 입력해주세요.');
	}
	
	

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'qna'}
            />
            <AdminQnAListForm
                headerText={'상품 문의'}
                data={data}
                typeSelectData={typeSelectData}
                thText={thText}
                handleSelectOnChange={handleSelectOnChange}
                handleOnClick={handleOnClick}
                handleKeywordOnChange={handleKeywordOnChange}
                keywordInput={keywordInput}
                pagingData={pagingData}
				handlePageBtn={handlePageBtn}
				handleSearchBtn={handleSearchBtn}
            />
        </div>
    )
}

export default AdminProductQnAList;