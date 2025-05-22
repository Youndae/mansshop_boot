import React from 'react';

import Pagination from '../../../common/components/Pagination';

// 관리자 리뷰 목록 List Form
function AdminReviewListForm(props) {
	const { 
		header,
        data,
        handleOnClick,
        keywordSelectValue,
        handleSelectOnChange,
        handleKeywordOnChange,
        keywordInput,
        pagingData,
        handlePageBtn,
		handleSearchOnClick
    } = props;

	return (
        <div className="admin-content">
            <div className="admin-content-header">
                <h1>{header}</h1>
            </div>
            <div className="admin-content-content">
                <table className="admin-content-table">
                    <thead>
                        <tr>
                            <th>상품명</th>
                            <th>작성자</th>
                            <th>최종 수정일</th>
                            <th>답변 상태</th>
                        </tr>
                    </thead>
                    <tbody>
                    {data.map((data, index) => {
						const reviewStatus = data.status ? '답변 완료' : '미답변';
						
                        return (
                            <tr key={index} onClick={() => handleOnClick(data.reviewId)} className="admin-order-body-tr">
                                <td>{data.productName}</td>
                                <td>{data.writer}</td>
                                <td>{data.updatedAt}</td>
                                <td>{reviewStatus}</td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
                <div className="admin-search">
                    <select className="admin-order-search" value={keywordSelectValue} onChange={handleSelectOnChange}>
                        <option value='user'>작성자</option>
                        <option value='product'>상품명</option>
                    </select>
                    <input type={'text'} onChange={handleKeywordOnChange} value={keywordInput}/>
                    <img alt={''} src={"https://as1.ftcdn.net/v2/jpg/03/25/73/68/1000_F_325736897_lyouuiCkWI59SZAPGPLZ5OWQjw2Gw4qY.jpg"} onClick={handleSearchOnClick}/>
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

export default AdminReviewListForm;