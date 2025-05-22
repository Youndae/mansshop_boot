import React, {useEffect, useState} from 'react';
import {useParams} from "react-router-dom";

import { 
	getProductQnADetail,
	patchProductQnAReply,
	postProductQnAReply,
	patchProductQnAComplete
} from '../../services/adminQnAService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';
import { toggleReplyInputStatus } from '../../../../common/utils/qnaUtils';

import AdminSideNav from '../../components/AdminSideNav';
import QnADetail from '../../../../common/components/QnADetail';

/*
	상품 문의 상세 페이지.

	내용 하단에서 답변 작성 가능.
	오른쪽 상단의 답변 완료 버튼을 통해 답변을 작성하지 않고 완료 처리 가능.
*/
function AdminProductQnADetail() {
	const { qnaId } = useParams();

	const [data, setData] = useState({
        productQnAId: '',
        title: '',
        writer: '',
        qnaContent: '',
        date: '',
        qnaStatus: ''
    });
    const [replyData, setReplyData] = useState([]);
    const [modifyTextValue, setModifyTextValue] = useState('');
    const [inputValue, setInputValue] = useState('');

	const getDetail = async () => {
		try {
			const res = await getProductQnADetail(qnaId);

			setData({
				productQnAId: res.data.productQnAId,
				title: `상품명 : ${res.data.productName}`,
				writer: res.data.writer,
				qnaContent: res.data.qnaContent,
				date: res.data.createdAt,
				qnaStatus: res.data.productQnAStat
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
		} catch(err) {
			console.log(err);
		}
	}

	useEffect(() => {
		getDetail();
	}, [qnaId]);
	
	//답변 수정 및 닫기 버튼 이벤트
	//답변 수정 Element Open, Close 제어
	const handleReplyModifyElement = (e) => {
		const idx = Number(e.target.value);
		toggleReplyInputStatus(replyData, idx, setReplyData, setModifyTextValue);
	}

	//답변 수정 textarea 입력 이벤트
    const handleModifyOnChange = (e) => {
        setModifyTextValue(e.target.value);
    }

	//답변 수정 submit 이벤트
	const handleModifySubmit = async (e) => {
		const idx = Number(e.target.value);
		const replyId = replyData[idx].replyId;

		try {
			const res = await patchProductQnAReply(replyId, modifyTextValue);

			if(res.data.message === RESPONSE_MESSAGE.OK) 
				getDetail();
		} catch(err) {
			console.log(err);
		}
	}
	
	//답변 작성 textarea 입력 이벤트
    const handleInputOnChange = (e) => {
        setInputValue(e.target.value);
    }

	//답변 작성 submit 이벤트
	const handleInputSubmit = async (e) => {
		try {
			const res = await postProductQnAReply(qnaId, inputValue);

			if(res.data.message === RESPONSE_MESSAGE.OK) 
				getDetail();
		} catch(err) {
			console.log(err);
		}
	}

	// 답변 완료 버튼 이벤트
	const handleCompleteBtn = async () => {
		try {
			const res = await patchProductQnAComplete(qnaId);

			if(res.data.message === RESPONSE_MESSAGE.OK) 
				getDetail();
		} catch(err) {
			console.log(err);
		}
	}

	return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'qna'}
            />
            <QnADetail
                data={data}
                replyData={replyData}
                handleReplyModifyElement={handleReplyModifyElement}
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