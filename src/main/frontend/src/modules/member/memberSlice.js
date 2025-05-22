import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    loginStatus: false,
    id: null,
    role: null,
};

const memberSlice = createSlice({
    name: 'member',
    initialState,
    reducers: {
        login(state, action) {
            const { userId, role } = action.payload;
            state.loginStatus = true;
            state.id = userId;
            state.role = role;
        },
        logout(state) {
            state.loginStatus = false;
            state.id = null;
            state.role = null;
        },
    },
});

export const { login, logout } = memberSlice.actions;
export default memberSlice.reducer;