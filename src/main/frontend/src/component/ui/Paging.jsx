import React from 'react';
import styled from "styled-components";
import { useNavigate } from "react-router-dom";


import PagingButton from './PagingButton';

const PagingliWrapper = styled.li`
    list-style: none;
    float: left;
    padding: 10px;
    cursor: pointer;
`

function Paging(props) {
    const { pagingData, keyword } = props;
    const navigate = useNavigate();
    let prevElem = null;
    let nextElem = null;
    const pagingNumberArr = [];

    if(pagingData.endPage !== 1){
        for(let i = pagingData.startPage; i <= pagingData.endPage; i++){
            let pagingClassName = 'pagingNumber';

            if(i === Number(pagingData.activeNo))
                pagingClassName = pagingClassName + ' active';

            const body = {
                pageNum: i,
                className: pagingClassName,
            }

            pagingNumberArr.push(body);
        }
    }

    if(pagingData.prev)
        prevElem = <PagingliWrapper>
            <PagingButton
                btnText={'prev'}
                className={'pagingPrev'}
                onClick={handlePagePrevBtnOnClick}
            />
        </PagingliWrapper>;

    if(pagingData.next)
        nextElem = <PagingliWrapper>
            <PagingButton
                btnText={'next'}
                className={'pagingNext'}
                onClick={handlePageNextBtnOnClick}
            />
        </PagingliWrapper>;

    const handlePageNoBtnOnClick = (e) => {
        const clickNo = e.target.textContent;

        paginationNavigate(clickNo);
    }

    const handlePagePrevBtnOnClick = () => {
        const prevNumber = pagingData.startPage - 1;

        paginationNavigate(prevNumber);
    }

    const handlePageNextBtnOnClick = () => {
        const nextNumber = pagingData.endPage + 1;

        paginationNavigate(nextNumber);
    }

    const paginationNavigate = (clickNo) => {
        navigate(`?page=${clickNo}`);
    }


    return(
        <div className="paging">
            <ul>
                {prevElem}
                {pagingNumberArr.map((pagingNum, index) => {
                    return(
                        <PagingNumber
                            key={index}
                            pagingNumberData={pagingNum}
                            btnOnClick={handlePageNoBtnOnClick}
                        />
                    )
                })}
                {nextElem}
            </ul>
        </div>
    )
}

function PagingNumber(props) {
    const { pagingNumberData, btnOnClick } = props;

    return (
        <PagingliWrapper>
            <PagingButton
                btnText={pagingNumberData.pageNum}
                className={pagingNumberData.className}
                onClick={btnOnClick}
            />
        </PagingliWrapper>
    )
}

export default Paging;
