import React from "react";
import {useNavigate} from "react-router-dom";

import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    searchTypePageSubmit,
    searchTypeSubmit,
    typePageSubmit
} from "../../../modules/pagingModule";

import Paging from "../../ui/Paging";

function AdminQnAListForm(props) {
    const { headerText, data, typeSelectData, thText, handleSelectOnChange, handleOnClick, handleKeywordOnChange, keyword, keywordInput, pagingData } = props;

    const navigate = useNavigate();

    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    const handlePagingSubmit = (pageNum) => {
        if(keyword === null)
            typePageSubmit(typeSelectData, pageNum, navigate);
        else
            searchTypePageSubmit(typeSelectData, keyword, pageNum, navigate);
    }

    const handleSearchOnClick = async () => {
        searchTypeSubmit('all', keywordInput, navigate);
    }

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
                        let statusText = '미답변';
                        if(data.answerStatus)
                            statusText = '답변 완료';

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

export default AdminQnAListForm;