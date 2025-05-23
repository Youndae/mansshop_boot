import React, { useEffect, useState } from 'react';

import { 
	getQnAClassificationList, 
	postQnAClassification, 
	deleteQnAClassification 
} from '../../services/adminQnAService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';

import AdminSideNav from '../../components/AdminSideNav';
import DefaultButton from '../../../../common/components/DefaultButton';

/*
    회원 문의에 사용되는 카테고리 관리 페이지
    현재 설정된 회원 문의 카테고리 리스트 출력
    추가 버튼을 통해 새로운 문의 카테고리 추가 가능
*/
function AdminQnAClassification() {
    const [data, setData] = useState([]);
    const [inputStatus, setInputStatus] = useState(false);
    const [inputValue, setInputValue] = useState('');

	const getList = async() => {
		try {
			const res = await getQnAClassificationList();
			setData(res.data);
		} catch(err) {
			console.log(err);
		}
	}

	useEffect(() => {
		getList();
	}, []);

	//추가 버튼 이벤트
    //추가 elements 출력
    const handleAddBtn = () => {
        setInputStatus(!inputStatus);
    }

    //추가시 문의명 input 입력 이벤트
    const handleOnChange = (e) => {
        setInputValue(e.target.value);
    }

	// 문의 분류 추가 이벤트
	const handleSubmit = async() => {
		try {
			const res = await postQnAClassification(inputValue);

			if(res.data.message === RESPONSE_MESSAGE.OK) {
				getList();
				setInputStatus(false);
				setInputValue('');
			}
		} catch(err) {
			console.log(err);
		}
	}

	// 문의 분류 제거 이벤트
	const handleDelete = async (e) => {
		const classificationName = e.target.value;

		try {
			const res = await deleteQnAClassification(classificationName);

			if(res.data.message === RESPONSE_MESSAGE.OK)
				getList();
		} catch(err) {
			console.log(err);
		}
	}

	return (
		<div className="mypage">
            <AdminSideNav
                categoryStatus={'qna'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>문의 카테고리 설정</h1>
                    <QnAClassificationHeaderBtn
                        status={inputStatus}
                        handleAddBtn={handleAddBtn}
                    />
                </div>
                <div className="admin-content-content admin-qna-classification-content">
                    <QnAClassificationInput
                        status={inputStatus}
                        value={inputValue}
                        onChange={handleOnChange}
                        onClick={handleSubmit}
                    />
                    {data.map((value, index) => {
                        return (
                            <div key={index} className="admin-qna-classification">
                                <div className="admin-qna-classification-info">
                                    <label>카테고리명 : </label>
                                    <span className="admin-qna-classification-name">{value.name}</span>
                                </div>
                                <div className="admin-qna-classification-delete-btn">
                                    <DefaultButton
                                        btnText={'카테고리 삭제'}
                                        onClick={handleDelete}
                                        value={value.id}
                                    />
                                </div>
                            </div>
                        )
                    })}
                </div>
            </div>
        </div>
	)
}

function QnAClassificationInput(props) {
    const { status, inputValue, onChange, onClick } = props;

    if(status) {
        return (
            <div className="admin-qna-classification-input">
                <div className="classification-input-content">
                    <label>카테고리명 : </label>
                    <input type={'text'} className="admin-classification-input" value={inputValue} onChange={onChange}/>
                </div>
                <div className="classification-input-btn">
                    <DefaultButton
                        btnText={'추가'}
                        className={'classification-submit-btn'}
                        onClick={onClick}
                    />
                </div>
            </div>
        )
    }else{
        return null;
    }
}

function QnAClassificationHeaderBtn (props) {
    const { status, handleAddBtn } = props;

	const btnText = status ? '닫기' : '카테고리 추가';

	return (
		<DefaultButton
			btnText={btnText}
			className={'admin-qna-classification-add-btn'}
			onClick={handleAddBtn}
		/>
	)
}

export default AdminQnAClassification;