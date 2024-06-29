import React, {useState, useEffect} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {productDetailPagingObject} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";
import AdminSideNav from "../../ui/nav/AdminSideNav";
import AdminQnAListForm from "./AdminQnAListForm";


/*
        회원문의.

        테이블구조.
        문의 분류, 제목, 답변상태, 작성일(또는 수정일)

        서버에서는 작성일과 수정일 중 최근 날짜를 가져온다.

        사용자가 답변 작성 시 수정일 날짜가 수정되어야 하며, 답변 상태를 다시 미답변으로 돌린다.

        테이블 오른쪽 상단에는 상품 문의와 마찬가지로 select box를 통해 미답변, 전체를 택할 수 있게 한다.

     */
function AdminMemberQnA() {
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
        getMemberQnA(page, keyword, typeSelectData);
        thTextSet();
    }, [page, keyword]);

    const thTextSet = () => {
        const textArr = [];

        textArr.push('문의 분류');
        textArr.push('제목');
        textArr.push('작성자');
        textArr.push('작성일');
        textArr.push('답변 상태');

        setThText(textArr);
    }

    const getMemberQnA = async (page, keyword, typeSelectData) => {
        let url = `admin/qna/member?page=${page}&type=${typeSelectData}`;
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
        getMemberQnA(1, keyword, value);
    }

    const handleOnClick = (qnaId) => {
        navigate(`/admin/qna/member/${qnaId}`);
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
                keyword={keyword}
                keywordInput={keywordInput}
                pagingData={pagingData}
            />
        </div>
    )

}

export default AdminMemberQnA;