import React, {useState, useEffect} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";

import { getMemberQnAList } from '../../services/adminQnAService';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';
import { buildQueryString } from '../../../../common/utils/queryStringUtils';

import AdminSideNav from '../../components/AdminSideNav';
import AdminQnAListForm from '../../components/AdminQnAListForm';

/*
	회원문의.

	테이블구조.
	문의 분류, 제목, 답변상태, 작성일(또는 수정일)

	서버에서는 작성일과 수정일 중 최근 날짜를 가져온다.

	사용자가 답변 작성 시 수정일 날짜가 수정되어야 하며, 답변 상태를 다시 미답변으로 돌린다.

	테이블 오른쪽 상단에는 상품 문의와 마찬가지로 select box를 통해 미답변, 전체를 택할 수 있음.

	params 중 type은 all과 new 만 가능하며 미답변 목록과 전체 목록으로 구분해서 조회 처리.
*/
function AdminMemberQnAList() {
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
			const res = await getMemberQnAList(page, keyword, type);

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
		//리스트 th 구조 정의
		//MemberQnA와 ProductQnA의 List Component를 같이 사용하기 때문에
		//th에 대한 구조를 정의해서 하위 컴포넌트로 보내기 위함.
		const thTextSet = () => {
			const textArr = [];

			textArr.push('문의 분류');
			textArr.push('제목');
			textArr.push('작성자');
			textArr.push('작성일');
			textArr.push('답변 상태');

			setThText(textArr);
		}

		thTextSet();
		window.scrollTo(0, 0);
		setTypeSelectData(type);
		getQnAList(page, keyword, type);
	}, [page, keyword, type]);

	//검색 input 입력 이벤트
    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    // 목록 타입 select box 이벤트
    // 미답변, 전체 중 선택된 목록을 조회
    const handleSelectOnChange = (e) => {
        const value = e.target.value;

        navigate(`/admin/qna/member?type=${value}`)
    }

    //상세 페이지 이동 이벤트
    const handleOnClick = (qnaId) => {
        navigate(`/admin/qna/member/${qnaId}`);
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
                headerText={'회원 문의'}
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

export default AdminMemberQnAList;