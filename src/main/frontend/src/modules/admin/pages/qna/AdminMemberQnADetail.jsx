import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

import { 
	getMemberQnADetail,
	patchMemberQnAReply,
	postMemberQnAReply,
	patchMemberQnAComplete
} from '../../services/adminQnAService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';
import { toggleReplyInputStatus } from '../../../../common/utils/qnaUtils';

import AdminSideNav from '../../components/AdminSideNav';
import QnADetail from '../../../../common/components/QnADetail';

/*
	회원 문의 상세.

	title 위치에 분류와 제목 작성일 또는 수정일

	content에는 내용

	하단에는 답변을 작성할 수 있도록 한다.

	관리자가 답변을 달기 애매한 마무리의 경우(감사 인사 등)의 처리를 위해 답변 완료 버튼을 추가한다.
*/
function AdminMemberQnADetail() {
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

	const getDetail = async () => {
		try {
			const res = await getMemberQnADetail(qnaId);

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
			const res = await patchMemberQnAReply(replyId, modifyTextValue);

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
			const res = await postMemberQnAReply(qnaId, inputValue);

			if(res.data.message === RESPONSE_MESSAGE.OK)
				getDetail();
		} catch(err) {
			console.log(err);
		}
	}

	//답변 완료 버튼 이벤트
	const handleCompleteBtn = async () => {
		try {
			const res = await patchMemberQnAComplete(qnaId);

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
                titleText={'회원 문의'}
                type={'member'}
                handleCompleteBtn={handleCompleteBtn}
            />
        </div>
    )
}

export default AdminMemberQnADetail;