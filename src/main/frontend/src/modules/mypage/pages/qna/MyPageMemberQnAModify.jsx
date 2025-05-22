import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import {
	getMemberQnAPatchData,
	patchMemberQnA
} from '../../services/mypageQnAService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';

import MyPageSideNav from '../../components/MyPageSideNav';
import MemberQnAWriteForm from '../../components/MyPageMemberQnAWriteForm';

//문의 수정 페이지
function MyPageMemberQnAModify() {
	const [inputData, setInputData] = useState({
        title: '',
        content: '',
    });
    const [classification, setClassification] = useState([]);
    const [classificationId, setClassificationId] = useState('');
    const { qnaId } = useParams();

    const navigate = useNavigate();

	useEffect(() => {
		const getPatchData = async() => {
			try {
				const res = await getMemberQnAPatchData(qnaId);

				console.log('modify qna res : ', res);

				setInputData({
                    title: res.data.qnaTitle,
                    content: res.data.qnaContent
                });

                setClassificationId(res.data.qnaClassificationId);
                setClassification(res.data.classificationList);
			} catch(err) {
				console.log(err);
			}
		}

		getPatchData();
	}, [qnaId]);
	
	//문의 수정 이벤트
	const handleSubmit = async() => {
		try {
			const res = await patchMemberQnA(qnaId, inputData, classificationId);

			if(res.data.message === RESPONSE_MESSAGE.OK)
				navigate(`/my-page/qna/member/detail/${qnaId}`);
		} catch(err) {
			console.log(err);
			alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
		}
	}

	//문의 분류 select box 이벤트
	const handleSelectOnChange = (e) => {
		setClassificationId(e.target.value);
	}

	//제목, textarea 입력 이벤트
	const handleInputOnChange = (e) => {
		setInputData({
			...inputData,
			[e.target.name]: e.target.value
		});
	}
	
	return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>문의하기</h1>
                </div>
                <MemberQnAWriteForm
                    inputData={inputData}
                    classificationId={classificationId}
                    classification={classification}
                    handleInputOnChange={handleInputOnChange}
                    handleSelectOnChange={handleSelectOnChange}
                    handleSubmit={handleSubmit}
                    btnText={'수정'}
                />
            </div>
        </div>
    )
}

export default MyPageMemberQnAModify;