import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import QnADetail from "../mypage/QnADetail";


/*
        params로 ProductQnAId를 받는다.

        title로 상품명.
        content로 문의 내용이 출력된다.

        하단에는 textarea로 답변을 작성할 수 있으며
        관리자가 답변을 작성하게 되면 답변 상태를 변경시킨다.
        오타나 잘못 작성하는 경우를 대비해 이미 작성된 답변에 대해 수정 처리를 할 수 있도록 처리힌다.

        관리자가 답변을 달기 애매한 마무리의 경우(감사 인사 등)의 처리를 위해 답변 완료 버튼을 추가한다.
     */
function AdminProductQnADetail() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const { qnaId } = useParams();

    const [data, setData] = useState({
        productQnAId: ''
        , title: ''
        , writer: ''
        , qnaContent: ''
        , date: ''
        , qnaStatus: ''
    });
    const [replyData, setReplyData] = useState([]);
    const [modifyTextValue, setModifyTextValue] = useState('');
    const [inputValue, setInputValue] = useState('');

    const dispatch = useDispatch();

    useEffect(() => {
        getProductQnADetail();
    }, [qnaId]);

    const getProductQnADetail = async () => {
        await axiosInstance.get(`admin/qna/product/${qnaId}`)
            .then(res => {
                setData({
                    productQnAId: res.data.productQnAId
                    , title: `상품명 : ${res.data.productName}`
                    , writer: res.data.writer
                    , qnaContent: res.data.qnaContent
                    , date: res.data.createdAt
                    , qnaStatus: res.data.productQnAStat
                })

                let replyArr = [];
                const replyList = res.data.replyList;

                for(let i = 0; i < replyList.length; i++) {
                    replyArr.push({
                        replyId: replyList[i].replyId,
                        writer: replyList[i].writer,
                        replyContent: replyList[i].replyContent,
                        updatedAt: replyList[i].updatedAt,
                        inputStatus: false,
                    });
                }

                setReplyData(replyArr);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleReplyModifyOpen = (e) => {
        setReplyModifyStatus(e, true);
    }

    const handleReplyModifyClose = (e) => {
        setReplyModifyStatus(e, false);
    }

    const setReplyModifyStatus = (e, status) => {
        const idx = e.target.value;

        replyData[idx] = {
            replyId: replyData[idx].replyId,
            writer: replyData[idx].writer,
            replyContent: replyData[idx].replyContent,
            updatedAt: replyData[idx].updatedAt,
            inputStatus: status,
        }

        setReplyData([...replyData]);

        if(status)
            setModifyTextValue(replyData[idx].replyContent);
    }

    const handleModifyOnChange = (e) => {
        setModifyTextValue(e.target.value);
    }

    const handleModifySubmit = async (e) => {
        const idx = e.target.value;
        const replyId = replyData[idx].replyId;

        await axiosInstance.patch(`admin/qna/product/reply`, {
            replyId: replyId
            , content: modifyTextValue,
        }, {
            headers: {'Content-Type': 'application/json'},
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    getProductQnADetail();
            })
    }

    const handleInputOnChange = (e) => {
        setInputValue(e.target.value);
    }

    const handleInputSubmit = async () => {
        await axiosInstance.post(`admin/qna/product/reply`, {
            qnaId: data.productQnAId,
            content: inputValue,
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    getProductQnADetail();
            })
    }

    const handleCompleteBtn = async () => {

        await axiosInstance.patch(`admin/qna/product/${data.productQnAId}`)
            .then(res => {
                if(checkResponseMessageOk(res))
                    getProductQnADetail();
            })
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'qna'}
            />
            <QnADetail
                data={data}
                replyData={replyData}
                handleReplyModifyOpen={handleReplyModifyOpen}
                handleReplyModifyClose={handleReplyModifyClose}
                handleModifyOnChange={handleModifyOnChange}
                modifyTextValue={modifyTextValue}
                handleModifySubmit={handleModifySubmit}
                handleInputOnChange={handleInputOnChange}
                inputValue={inputValue}
                handleInputSubmit={handleInputSubmit}
                titleText={'상품 문의'}
                handleCompleteBtn={handleCompleteBtn}
            />
        </div>
    )
}

export default AdminProductQnADetail;