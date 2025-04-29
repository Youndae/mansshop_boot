import React, {useEffect, useState} from 'react';
import {useNavigate, useParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import QnADetail from "./QnADetail";

/*
    회원 문의 상세 페이지
    답변 작성 및 수정이 가능
    회원이 답변을 작성하는 경우 다시 미답변으로 수정 됨.
 */
function MemberQnADetail() {
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

    const navigate = useNavigate();

    useEffect(() => {
        getMemberQnADetail();
    }, [qnaId]);

    //문의 상세 정보 조회
    const getMemberQnADetail = async () => {

        await axiosInstance.get(`my-page/qna/member/detail/${qnaId}`)
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
            })
    }

    //답변 수정 폼 오픈 이벤트
    const handleReplyModifyOpen = (e) => {
        setReplyModifyStatus(e, true);
    }

    //답변 수정 폼 close 이벤트
    const handleReplyModifyClose = (e) => {
        setReplyModifyStatus(e, false);
    }

    //답변 수정 폼 세팅
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

    //답변 수정 textarea 입력 이벤트
    const handleModifyOnChange = (e) => {
        setModifyTextValue(e.target.value);
    }

    //답변 수정 submit 이벤트
    const handleModifySubmit = async (e) => {
        const idx = e.target.value;
        const replyId = replyData[idx].replyId;

        await axiosInstance.patch(`my-page/qna/member/reply`, {
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

    //답변 작성 input 입력 이벤트
    const handleInputOnChange = (e) => {
        setInputValue(e.target.value);
    }

    //답변 작성 입력 submit 이벤트
    const handleInputSubmit = async () => {
        await axiosInstance.post(`my-page/qna/member/reply`, {
            qnaId: data.memberQnAId,
            content: inputValue,
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    getMemberQnADetail();
            })
    }

    //문의 삭제 버튼 이벤트
    const handleDeleteBtn = async () => {

        await axiosInstance.delete(`my-page/qna/member/${data.memberQnAId}`)
            .then(res => {
                if(checkResponseMessageOk(res))
                    navigate('/my-page/qna/member')
            })
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
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
                titleText={'문의 사항'}
                type={'member'}
                handleDeleteBtn={handleDeleteBtn}
            />
        </div>
    )
}

export default MemberQnADetail;