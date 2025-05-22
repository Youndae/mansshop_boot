import React,  { useEffect, useRef, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { getReviewList, deleteReview } from '../../services/mypageReviewService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';

import MyPageSideNav from '../../components/MyPageSideNav';
import Pagination from '../../../../common/components/Pagination';
import ImageForm from '../../../../common/components/ImageForm';
import MyPageModal from '../../components/modal/MyPageModal';

//작성한 리뷰 목록
function MyPageReview() {
	const [params] = useSearchParams();
	const { page = 1 } = Object.fromEntries(params);

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

	const getReview = async() => {
		try {
			const res = await getReviewList(page);
			
			setData(res.data.content);

			const pagingObject = mainProductPagingObject(page, res.data.totalPages);

			setPagingData({
				startPage: pagingObject.startPage,
				endPage: pagingObject.endPage,
				prev: pagingObject.prev,
				next: pagingObject.next,
				activeNo: pagingObject.activeNo,
			});
		} catch (error) {
			console.log(error);
		}
	}

	useEffect(() => {
		window.scrollTo(0, 0);
		getReview();
	}, [page]);

	// 페이지네이션 버튼 이벤트
	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
		});
	}

	// 리뷰 삭제 이벤트
	const handleDeleteReview = async(e) => {
		if(window.confirm('리뷰를 삭제하시겠습니까?\n삭제 이후 재작성은 불가합니다.')){
			const reviewId = e.target.name;

			try {
				const res = await deleteReview(reviewId);
				if(res.data.message === RESPONSE_MESSAGE.OK)
					getReview();
			} catch (error) {
				console.log(error);
			}
		}
	}

	//목록에서 리뷰 클릭 이벤트.
	//Open Modal
	const handleReviewOnClick = (e) => {
		const idx = e.target.id;
		setModalData(data[idx]);
		setIsOpen(true);
	}

	//Modal 닫기 이벤트
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
                <Pagination
                    pagingData={pagingData}
                    handlePageBtn={handlePageBtn}
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
                    <ImageForm imageName={data.thumbnail} className={'mypage-like-thumbnail'} />
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