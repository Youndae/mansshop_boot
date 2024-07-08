import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import QnADetail from "../mypage/QnADetail";


/*
        회원 문의 상세.

        title 위치에 분류와 제목 작성일 또는 수정일

        content에는 내용

        하단에는 답변을 작성할 수 있도록 한다.

        관리자가 답변을 달기 애매한 마무리의 경우(감사 인사 등)의 처리를 위해 답변 완료 버튼을 추가한다.
     */
function AdminMemberQnADetail() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const { qnaId } = useParams();

    const [data, setData] = useState({
        memberQnAId: ''
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
    const navigate = useNavigate();

    useEffect(() => {
        getMemberQnADetail();
    }, []);

    const getMemberQnADetail = async () => {
        await axiosInstance.get(`admin/qna/member/${qnaId}`)
            .then(res => {
                setData({
                    memberQnAId: res.data.memberQnAId
                    , title: `[${res.data.qnaClassification}] ${res.data.qnaTitle}`
                    , writer: res.data.writer
                    , qnaContent: res.data.qnaContent
                    , date: res.data.updatedAt
                    , qnaStatus: res.data.memberQnAStat
                });

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

        await axiosInstance.patch(`admin/qna/member/reply`, {
            replyId: replyId
            , content: modifyTextValue,
        }, {
            headers: {'Content-Type': 'application/json'},
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    getMemberQnADetail();
            })
    }

    const handleInputOnChange = (e) => {
        setInputValue(e.target.value);
    }

    const handleInputSubmit = async () => {
        await axiosInstance.post(`admin/qna/member/reply`, {
            qnaId: data.memberQnAId,
            content: inputValue,
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    getMemberQnADetail();
            })
    }

    const handleCompleteBtn = async () => {

        await axiosInstance.patch(`admin/qna/member/${data.memberQnAId}`)
            .then(res => {
                if(checkResponseMessageOk(res))
                    getMemberQnADetail();
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
                titleText={'회원 문의'}
                type={'member'}
                handleCompleteBtn={handleCompleteBtn}
            />
        </div>
    )
}

export default AdminMemberQnADetail;