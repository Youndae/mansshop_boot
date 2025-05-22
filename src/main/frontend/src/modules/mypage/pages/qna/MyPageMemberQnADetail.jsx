import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { 
	getMemberQnADetail, 
	deleteMemberQnA,
	modifyReply,
	postReply
} from '../../services/mypageQnAService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';
import { toggleReplyInputStatus } from '../../../../common/utils/qnaUtils';

import MyPageSideNav from '../../components/MyPageSideNav';
import QnADetail from '../../../../common/components/QnADetail';

function MyPageMemberQnADetail() {
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

	const getMemberQnA = async () => {
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
		getMemberQnA();
	}, [qnaId]);

	// 문의 삭제 버튼 이벤트
	const handleDeleteBtn = async() => {
		try {
			const res = await deleteMemberQnA(qnaId);

			if(res.data.message === RESPONSE_MESSAGE.OK)
				navigate('/my-page/qna/member');
			
		} catch(err) {
			console.log(err);
		}
	}

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
	const handleModifySubmit = async(e) => {
		const idx = e.target.value;
		const replyId = replyData[idx].replyId;

		try {
			const res = await modifyReply(replyId, modifyTextValue);

			if(res.data.message === RESPONSE_MESSAGE.OK) 
				getMemberQnA();
		} catch(err) {
			console.log(err);
			alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
		}
	}

	//답변 작성 input 입력 이벤트
	const handleInputOnChange = (e) => {
		setInputValue(e.target.value);
	}

	//답변 작성 입력 submit 이벤트
	const handleInputSubmit = async () => {
		try {
			const res = await postReply(data.memberQnAId, inputValue);

			if(res.data.message === RESPONSE_MESSAGE.OK) {
				setInputValue('');
				getMemberQnA();
			}
		} catch(err) {
			console.log(err);
			alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
		}
	}

	return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
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
                titleText={'문의 사항'}
                type={'member'}
                handleDeleteBtn={handleDeleteBtn}
            />
        </div>
    )
}

export default MyPageMemberQnADetail;