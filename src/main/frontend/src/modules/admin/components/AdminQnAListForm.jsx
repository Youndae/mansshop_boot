import React from 'react';

import Pagination from '../../../common/components/Pagination';

/*
    상품 및 회원 문의 List Component
    th 구조가 다르기 떄문에 각 Component에서 정의한 th 배열 데이터 필요.
*/
function AdminQnAListForm(props) {
	const { 
		headerText, 
		data, 
		typeSelectData, 
		thText, 
		handleSelectOnChange, 
		handleOnClick, 
		handleKeywordOnChange, 
		keywordInput, 
		pagingData,
		handlePageBtn,
		handleSearchBtn,
	} = props;

	return (
        <div className="admin-content">
            <div className="admin-content-header">
                <h1>{headerText}</h1>
                <select className="admin-qna-select-box" value={typeSelectData} onChange={handleSelectOnChange}>
                    <option value={'new'}>미처리 문의</option>
                    <option value={'all'}>전체 문의</option>
                </select>
            </div>
            <div className="admin-content-content">
                <table className="admin-content-table">
                    <thead>
                    <tr>
                        {thText.map((val, index) => {
                            return (
                                <th key={index}>{val}</th>
                            )
                        })}
                    </tr>
                    </thead>
                    <tbody>
                    {data.map((data, index) => {
						const statusText = data.answerStatus ? '답변 완료' : '미답변';

                        return (
                            <tr key={index} onClick={() => handleOnClick(data.qnaId)} className="admin-order-body-tr">
                                <td>{data.classification}</td>
                                <td>{data.title}</td>
                                <td>{data.writer}</td>
                                <td>{data.createdAt}</td>
                                <td>{statusText}</td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
                <div className="admin-search">
                    <input type={'text'} onChange={handleKeywordOnChange} value={keywordInput}/>
                    <img alt={''} src={"https://as1.ftcdn.net/v2/jpg/03/25/73/68/1000_F_325736897_lyouuiCkWI59SZAPGPLZ5OWQjw2Gw4qY.jpg"} onClick={handleSearchBtn}/>
                    <Pagination
                        pagingData={pagingData}
                        handlePageBtn={handlePageBtn}
                        className={'like-paging'}
                    />
                </div>
            </div>
        </div>
    )
}

export default AdminQnAListForm;