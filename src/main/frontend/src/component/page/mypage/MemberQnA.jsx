import React, {useEffect, useState} from 'react';
import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    pageSubmit,
    productDetailPagingObject
} from "../../../modules/pagingModule";
import Paging from "../../ui/Paging";
import DefaultBtn from "../../ui/DefaultBtn";

function MemberQnA() {
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
        getMemberQnA();
    }, [page]);

    const getMemberQnA = async () => {

        await axiosInstance.get(`my-page/qna/member/${page}`)
            .then(res => {
                console.log('memberQnA res : ', res);
                setQnAData(res.data.content);

                const pagingObject = productDetailPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: page,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('productQnA error : ', err);
            })
    }

    const handlePageBtn = (e) => {
        pageSubmit(getClickNumber(e), navigate);
        // handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        pageSubmit(getPrevNumber(pagingData), navigate);
        // handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        pageSubmit(getNextNumber(pagingData));
        // handlePagingSubmit(getNextNumber(pagingData));
    }

    /*const handlePagingSubmit = (pageNum) => {
        navigate(`/my-page/qna/member?page=${pageNum}`);
    }*/

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