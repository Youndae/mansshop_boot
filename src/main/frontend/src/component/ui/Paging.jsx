import React from 'react';
import styled from "styled-components";

import PagingButton from './PagingButton';

const PagingLiWrapper = styled.li`
    list-style: none;
    float: left;
    padding: 10px;
`

function Paging(props) {
    const { pagingData, onClickNumber, onClickPrev, onClickNext, className } = props;
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
        prevElem = <PagingLiWrapper>
            <PagingButton
                btnText={'prev'}
                className={'pagingPrev'}
                onClick={onClickPrev}
            />
        </PagingLiWrapper>;

    if(pagingData.next)
        nextElem = <PagingLiWrapper>
            <PagingButton
                btnText={'next'}
                className={'pagingNext'}
                onClick={onClickNext}
            />
        </PagingLiWrapper>;

    return(
        <div className={`paging ${className}`}>
            <ul>
                {prevElem}
                {pagingNumberArr.map((pagingNum, index) => {
                    return(
                        <PagingNumber
                            key={index}
                            pagingNumberData={pagingNum}
                            btnOnClick={onClickNumber}
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
        <PagingLiWrapper>
            <PagingButton
                btnText={pagingNumberData.pageNum}
                className={pagingNumberData.className}
                onClick={btnOnClick}
            />
        </PagingLiWrapper>
    )
}

export default Paging;
