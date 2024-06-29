import React from 'react';
import styled from "styled-components";

import PagingButton from './PagingButton';

const PagingLiWrapper = styled.li`
    list-style: none;
    float: left;
    padding: 10px;
`

/*
    페이징 완전 컴포넌트화 하면
    pagingData, className, prev, next, btn clickHandling을 받는다.

    페이징 사용처로 예상되는 곳
        메인 카테고리
        상세페이지 리뷰
        상세페이지 문의
        주문 조회 리스트
        관리자 대부분의 기능

    css를 생각해서 className을 설정한다고 가정했을 때
    paging을 기본으로 잡아 list-style, cursor, float을 default로 설정하도록 처리
    나머지는 각 기능에 대한 className을 받아 css를 적용해 위치를 잡아줄 수 있도록 처리

 */
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
