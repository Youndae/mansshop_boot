import React, {useEffect, useState} from 'react';
import {Link, useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject,
    pageSubmit
} from "../../../modules/pagingModule";
import {createPageParam} from "../../../modules/requestUrlModule";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import Paging from "../../ui/Paging";
import DefaultBtn from "../../ui/DefaultBtn";

/*
    회원 문의 목록
    회원 문의 작성은 여기에서 가능
 */
function MemberQnA() {
    const [params] = useSearchParams();
    const page = params.get('page');

    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [qnaData, setQnAData] = useState([]);

    const navigate = useNavigate();

    useEffect(() => {
        getMemberQnA();
    }, [page]);

    //회원 문의 목록 조회
    const getMemberQnA = async () => {

        await axiosInstance.get(`my-page/qna/member${createPageParam(page)}`)
            .then(res => {
                setQnAData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });
            })
    }

    //페이지네이션 버튼 이벤트
    const handlePageBtn = (e) => {
        pageSubmit(getClickNumber(e), navigate);
    }

    //페이지네이션 이전 버튼 이벤트
    const handlePagePrev = () => {
        pageSubmit(getPrevNumber(pagingData), navigate);
    }

    //페이지네이션 다음 버튼 이벤트
    const handlePageNext = () => {
        pageSubmit(getNextNumber(pagingData));
    }

    //문의 작성 페이지 이동 버튼 이벤트
    const handleInsertBtn = () => {
        navigate('/my-page/qna/member/write');
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>문의 사항</h1>
                    <div className="mypage-qna-header-btn">
                        <DefaultBtn onClick={handleInsertBtn} btnText={'문의하기'}/>
                    </div>
                </div>
                <div className="mypage-qna-content">
                    <table className="qna-table">
                        <thead>
                            <tr>
                                <th>분류</th>
                                <th>제목</th>
                                <th>답변 상태</th>
                                <th>작성일</th>
                            </tr>
                        </thead>
                        <tbody>
                        {qnaData.map((data, index) => {
                            return (
                                <ProductQnABody
                                    key={index}
                                    data={data}
                                />
                            )
                        })}
                        </tbody>
                    </table>
                </div>
                <Paging
                    pagingData={pagingData}
                    onClickNumber={handlePageBtn}
                    onClickPrev={handlePagePrev}
                    onClickNext={handlePageNext}
                    className={'like-paging'}
                />
            </div>
        </div>
    )
}
function ProductQnABody(props) {
    const { data } = props;
    let qnaStatText = '미답변';
    if(data.memberQnAStat)
        qnaStatText = '답변 완료';

    return (
        <tr>
            <td>{data.qnaClassification}</td>
            <td>
                <Link to={`/my-page/qna/member/detail/${data.memberQnAId}`}>
                    {data.memberQnATitle}
                </Link>
            </td>
            <td>{qnaStatText}</td>
            <td>{data.updatedAt}</td>
        </tr>
    )
}

export default MemberQnA;