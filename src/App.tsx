/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import CreateMeetingPage from './pages/CreateMeetingPage';
import SetPreferencesPage from './pages/SetPreferencesPage';
import ResultPage from './pages/ResultPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/create" replace />} />
        <Route path="/create" element={<CreateMeetingPage />} />
        <Route path="/meeting/:meetingId/preferences" element={<SetPreferencesPage />} />
        <Route path="/meeting/:meetingId/result" element={<ResultPage />} />
      </Routes>
    </BrowserRouter>
  );
}
