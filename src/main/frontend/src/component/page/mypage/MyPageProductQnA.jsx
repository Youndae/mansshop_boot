import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject,
    pageSubmit
} from "../../../modules/pagingModule";
import {createPageParam} from "../../../modules/requestUrlModule";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import Paging from "../../ui/Paging";


function MyPageProductQnA() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');

    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [qnaData, setQnAData] = useState([]);

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getProductQnA();
    }, [page]);

    const getProductQnA = async () => {
        await axiosInstance.get(`my-page/qna/product${createPageParam(page)}`)
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

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handlePageBtn = (e) => {
        pageSubmit(getClickNumber(e), navigate);
    }

    const handlePagePrev = () => {
        pageSubmit(getPrevNumber(pagingData), navigate);
    }

    const handlePageNext = () => {
        pageSubmit(getNextNumber(pagingData));
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={true}
            />
            <div className="mypage-content">
                <div className="mypage-qna-header">
                    <h1>상품 문의</h1>
                </div>
                <div className="mypage-qna-content">
                    <table className="qna-table">
                        <thead>
                            <tr>
                                <th>상품명</th>
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
    if(data.productQnAStat)
        qnaStatText = '답변 완료';

    return (
        <tr>
            <td>
                <Link to={`/my-page/qna/product/detail/${data.productQnAId}`}>
                    {data.productName}
                </Link>
            </td>
            <td>{qnaStatText}</td>
            <td>{data.createdAt}</td>
        </tr>
    )
}

export default MyPageProductQnA;