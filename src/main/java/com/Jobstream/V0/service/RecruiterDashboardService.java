package com.Jobstream.V0.service;

import com.Jobstream.V0.dto.response.RecruiterDashboardResponse;

import java.util.UUID;

public interface RecruiterDashboardService {

    RecruiterDashboardResponse getDashboard(UUID companyId, UUID recruiterId);
}
