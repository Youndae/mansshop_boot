import React from 'react';
import {
    BrowserRouter,
    Routes,
    Route
} from "react-router-dom";

import TestComponent from "./component/TestComponent";

function App() {
  return (
      <BrowserRouter>
        <Routes>
          <Route index element={<TestComponent />}/>
        </Routes>
      </BrowserRouter>
  )
}
export default App;
