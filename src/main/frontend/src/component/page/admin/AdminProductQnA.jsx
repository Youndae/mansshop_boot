import React, {useState, useEffect} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {productDetailPagingObject} from "../../../modules/pagingModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import AdminQnAListForm from "./AdminQnAListForm";

/*
        상품 문의 목록 컴포넌트.

        테이블 구조로 출력.

        상품 분류, 상품명, 작성자, 작성일, 답변 상태 구조로 출력.

        테이블 오른쪽 상단에 select box로 미답변 문의만 보기, 전체 문의 보기를 만들어준다.

        클릭 시 상세 페이지로 이동하도록 처리한다.

        검색은 사용자 아이디를 키워드로 검색.

     */
function AdminProductQnA() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
    const keyword = params.get('keyword');
    const list = params.get('type') == null ? 'new' : params.get('type');

    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [typeSelectData, setTypeSelectData] = useState(list);
    const [keywordInput, setKeywordInput] = useState('');
    const [thText, setThText] = useState([]);

    const navigate = useNavigate();
    const dispatch = useDispatch();

    useEffect(() => {
        getProductQnA(page, keyword, typeSelectData);
        thTextSet();
    }, [page, keyword]);

    const thTextSet = () => {
        const textArr = [];

        textArr.push('상품분류');
        textArr.push('상품명');
        textArr.push('작성자');
        textArr.push('작성일');
        textArr.push('답변 상태');

        setThText(textArr);
    }

    const getProductQnA = async (page, keyword, typeSelectData) => {
        let url = `admin/qna/product?page=${page}&type=${typeSelectData}`;
        if(keyword !== null)
            url += `&keyword=${keyword}`;

        await axiosInstance.get(url)
            .then(res => {
                setData(res.data.content);

                const pagingObject = productDetailPagingObject(page, res.data.totalPages);

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

    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    const handleSelectOnChange = (e) => {
        const value = e.target.value;

        setTypeSelectData(value);
        getProductQnA(1, keyword, value);
    }

    const handleOnClick = (qnaId) => {
        navigate(`/admin/qna/product/${qnaId}`);
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
                keyword={keyword}
                keywordInput={keywordInput}
                pagingData={pagingData}
            />
        </div>
    )
}

export default AdminProductQnA;