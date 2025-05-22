import React from 'react';
import { Link } from 'react-router-dom';


const DETAIL_URL = {
	MEMBER: '/my-page/qna/member/detail/',
	PRODUCT: '/my-page/qna/product/detail/',
}

// 마이페이지 회원 및 상품 목록 테이블 body 컴포넌트
function MyPageQnABody(props) {
	const { type, qnaClassification, qnaStatus, qnaTitle, qnaDate, qnaId } = props;

	let classification = null;
	if(qnaClassification)
		classification = <td>{qnaClassification}</td>;

	let statusText = '미답변';
	if(qnaStatus)
		statusText = '답변 완료';

	const detailUrl = DETAIL_URL[type] + qnaId;

	return (
		<tr>
			{classification}
			<td>
				<Link to={detailUrl}>{qnaTitle}</Link>
			</td>
			<td>{statusText}</td>
			<td>{qnaDate}</td>
		</tr>
	)
}

export default MyPageQnABody;