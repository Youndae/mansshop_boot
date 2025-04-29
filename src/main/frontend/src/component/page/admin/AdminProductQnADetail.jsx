import React, {useEffect, useState} from 'react';
import {useParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import QnADetail from "../mypage/QnADetail";


/*
        상품 문의 상세 페이지.

        내용 하단에서 답변 작성 가능.
        오른쪽 상단의 답변 완료 버튼을 통해 답변을 작성하지 않고 완료 처리 가능.
 */
function AdminProductQnADetail() {
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

    useEffect(() => {
        getProductQnADetail();
    }, [qnaId]);

    //상품 문의 상세 데이터 조회
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
            })
    }

    //답변 수정 버튼 이벤트
    //답변 수정 Element가 출력
    const handleReplyModifyOpen = (e) => {
        setReplyModifyStatus(e, true);
    }

    //답변 수정 Element 닫기 버튼 이벤트
    const handleReplyModifyClose = (e) => {
        setReplyModifyStatus(e, false);
    }

    //답변 수정 Element 제어
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

    //답변 수정 이벤트
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

    //답변 작성 textarea 입력 이벤트
    const handleInputOnChange = (e) => {
        setInputValue(e.target.value);
    }

    //답변 작성 이벤트
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

    //답변 완료 버튼 이벤트
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