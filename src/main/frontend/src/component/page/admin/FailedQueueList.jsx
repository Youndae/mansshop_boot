import React, {useState, useEffect} from 'react';

import {axiosInstance} from "../../../modules/customAxios";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import DefaultBtn from "../../ui/DefaultBtn";

function FailedQueueList() {
    const [data, setData] = useState([]);

    useEffect(() => {
        getFailedListCount();
    }, []);

    const getFailedListCount = async () => {
        await axiosInstance.get('admin/message')
            .then(res => {
                setData(res.data.content);
            })
    }

    const handleRetryBtn = () => {
        retryFailedQueue();
    }

    const retryFailedQueue = async () => {
        await axiosInstance.post('admin/message', data)
            .then(res => {
                if(res.data.message === 'OK')
                    alert('실패한 메시지를 재시도합니다.\n메시지량에 따라 처리 시간이 상이할 수 있습니다.');
            })
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'failedQueue'}
            />
            <div className="admin-content">
                <div className="admin-content-header admin-product-header">
                    <h1>처리 실패 메시지</h1>
                    <FailedQueueBtn
                        data={data}
                        handleRetryBtn={handleRetryBtn}
                    />
                </div>
                <div className="admin-content-content">
                    <FailedListData
                        data={data}
                    />
                </div>
            </div>
        </div>
    )
}

function FailedQueueBtn(props) {
    const { data, handleRetryBtn } = props;

    if(data.length === 0){
        return null;
    }else{
        return (
            <DefaultBtn
                btnText={'재시도'}
                onClick={handleRetryBtn}
            />
        )
    }
}

function FailedListData(props) {
    const { data } = props;

    if(data.length === 0) {
        return (
            <span>미처리된 실패 메시지가 존재하지 않습니다.</span>
        )
    }else {
        return (
            <table className="admin-content-table">
                <thead>
                    <tr>
                        <th>QueueName</th>
                        <th>메시지 개수</th>
                    </tr>
                </thead>
                <tbody>
                {data.map((data, index) => {
                    return (
                        <tr key={index} className="admin-order-body-tr">
                            <td>{data.queueName}</td>
                            <td>{data.messageCount}</td>
                        </tr>
                    )
                })}
                </tbody>
            </table>
        )
    }
}

export default FailedQueueList;