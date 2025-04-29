import React, {useEffect, useRef, useState} from "react";
import {Link, useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject,
    pageSubmit
} from "../../../modules/pagingModule";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import Paging from "../../ui/Paging";
import Image from "../../ui/Image";
import MyPageModal from "./MyPageModal";
import {createPageParam} from "../../../modules/requestUrlModule";

//작성한 리뷰 목록
function MyPageReview() {
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');

    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [data, setData] = useState([]);
    const [modalData, setModalData] = useState('');
    const [isOpen, setIsOpen] = useState(false);
    const modalRef = useRef(null);

    const navigate = useNavigate();

    useEffect(() => {
        getReview();
    }, [page]);

    //리뷰 목록 조회
    const getReview = async () => {

        await axiosInstance.get(`my-page/review?${createPageParam(page)}`)
            .then(res => {
                setData(res.data.content);

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

    //리뷰 제거 이벤트
    const handleDeleteReview = async (e) => {
        if(window.confirm('리뷰를 삭제하시겠습니까?\n삭제 이후 재작성은 불가합니다.')){
            const reviewId = e.target.name;

            await axiosInstance.delete(`my-page/review/${reviewId}`)
                .then(res => {
                    if(checkResponseMessageOk(res))
                        getReview();
                })
        }
    }

    //목록에서 리뷰 클릭 이벤트. Modal Open
    const handleReviewOnClick = (e) => {
        const idx = e.target.id;
        setModalData(data[idx]);
        setIsOpen(true);

    }

    //리뷰 상세 Modal close 이벤트
    const closeModal = (e) => {
        if(isOpen && modalRef.current && !modalRef.current.contains(e.target)){
            setIsOpen(false);
            document.body.style.cssText = '';
        }
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={false}
            />
            <div className="mypage-content">
                <div className="mypage-content-header">
                    <h1>리뷰 내역</h1>
                </div>
                <div className="mypage-like-content mypage-review-content">
                    <div className="mypage-like-content-list mypage-review-content-list">
                        {data.map((data, index) => {
                            return (
                                <div key={index} className="mypage-like-detail mypage-review-detail">
                                    <ReviewListContent
                                        data={data}
                                        index={index}
                                        handleDeleteReview={handleDeleteReview}
                                        handleReviewOnClick={handleReviewOnClick}
                                    />
                                </div>
                            )
                        })}
                    </div>
                </div>
                {isOpen && (
                    <MyPageModal
                        closeModal={closeModal}
                        data={modalData}
                        modalRef={modalRef}
                    />
                )}
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

function ReviewListContent(props) {
    const { data, index, handleDeleteReview, handleReviewOnClick } = props;

    let createdAt = `${data.createdAt}`;

    if(data.createdAt !== data.updatedAt)
        createdAt = `${data.createdAt} 작성, ${data.updatedAt} 수정`;

    return (
        <div className="mypage-like-data-detail">
            <div className="mypage-like-detail-content">
                <div className="mypage-like-remove">
                    <img className="mypage-like-delete-btn" src={`${process.env.PUBLIC_URL}/image/del.jpg`} name={data.reviewId}
                         onClick={handleDeleteReview} alt={''}/>
                </div>
                <div className="mypage-like-thumb" >
                    <Image imageName={data.thumbnail} className={'mypage-like-thumbnail'} />
                    <div className="mypage-like-info">
                        <div>
                            <span className="like-data-product-name" id={index} onClick={handleReviewOnClick}>{data.productName}</span>
                        </div>
                        <span className="mypage-review-detail-date">{createdAt}</span>
                    </div>
                </div>

            </div>
        </div>
    )
}

export default MyPageReview;