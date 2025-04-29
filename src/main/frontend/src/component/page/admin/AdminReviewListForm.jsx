import React from 'react';
import {useNavigate} from "react-router-dom";

import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    pageSubmit,
    searchTypePageSubmit, searchTypeSubmit,
} from "../../../modules/pagingModule";

import Paging from "../../ui/Paging";

/*
    관리자 리뷰 목록 List Component
 */
function AdminReviewListForm(props) {
    const { header
        , data
        , searchType
        , handleOnClick
        , keywordSelectValue
        , handleSelectOnChange
        , handleKeywordOnChange
        , keywordInput
        , pagingData
        , keyword
    } = props;

    const navigate = useNavigate();

    //페이지네이션 버튼 이벤트
    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    //페이지네이션 이전 버튼 이벤트
    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    //페이지네이션 다음 버튼 이벤트
    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    //페이지네이션 제어
    const handlePagingSubmit = (pageNum) => {
        if(keyword == null)
            pageSubmit(pageNum, navigate);
        else
            searchTypePageSubmit(searchType, keyword, pageNum, navigate);
    }

    //검색 이벤트
    const handleSearchOnClick = () => {
        searchTypeSubmit(keywordSelectValue, keywordInput, navigate);
    }

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
                        let reviewStatus = '미답변';
                        if(data.status)
                            reviewStatus = '답변 완료';
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
                    <Paging
                        pagingData={pagingData}
                        onClickNumber={handlePageBtn}
                        onClickPrev={handlePagePrev}
                        onClickNext={handlePageNext}
                        className={'like-paging'}
                    />
                </div>
            </div>
        </div>
    )
}

export default AdminReviewListForm;