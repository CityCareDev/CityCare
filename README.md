# CityCare - Urban Healthcare & Emergency Medical Response System

## Tech Stack
- **Java 21** + Spring Boot 3.3.4
- Spring Security (JWT)
- Spring Data JPA + Hibernate (auto DDL)
- MySQL 8+
- Lombok
- SpringDoc OpenAPI (Swagger UI)

## Quick Start

### 1. Create MySQL Database
```sql
CREATE DATABASE Citycare;
```

### 2. Insert First Admin User
```sql
USE Citycare;
INSERT INTO users (name, email, password, role, status, created_at)
VALUES ('Admin User', 'admin@citycare.com',
  '$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lHHi',
  'ADMIN', 'ACTIVE', NOW());
```
Password: `admin123`

### 3. Configure Database
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. Run
```bash
mvn spring-boot:run
```
App starts at: http://localhost:9090/api

### 5. Swagger UI
http://localhost:9090/api/swagger-ui.html

---

## Module Endpoints

### Authentication (Public)
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | /auth/register | Public | Citizen self-registration |
| POST | /auth/login | Public | Login for all roles |

### Emergency Response
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | /emergencies/report | CITIZEN | Report emergency |
| GET | /emergencies/my | CITIZEN | View my emergency history |
| GET | /emergencies/pending | DISPATCHER | View pending emergencies |
| GET | /emergencies/ambulances/available | DISPATCHER | View available ambulances |
| POST | /emergencies/{id}/dispatch | DISPATCHER | Assign ambulance |
| GET | /emergencies/dispatched | ADMIN | View dispatched emergencies |
| GET | /emergencies/{id} | Any | Get emergency detail |

### Patients
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | /patients/admit | ADMIN | Admit patient (auto-releases ambulance) |
| GET | /patients | Any | List all patients |
| GET | /patients/{id} | Any | Get patient detail |
| PATCH | /patients/{id}/status | DOCTOR/NURSE | Update patient status |
| GET | /patients/status/{status} | Any | Filter by status |

### Treatments
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | /treatments | DOCTOR/NURSE | Assign treatment |
| GET | /treatments/patient/{id} | DOCTOR/NURSE | Get treatments for patient |
| PATCH | /treatments/{id}/status | DOCTOR/NURSE | Update treatment status |
| GET | /treatments/mine | DOCTOR/NURSE | My assigned treatments |

### Admin
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | /admin/staff | ADMIN | Create DOCTOR/NURSE account |
| GET | /admin/staff | ADMIN | List all staff |
| POST | /admin/dispatchers | ADMIN | Create DISPATCHER account |
| GET | /admin/dispatchers | ADMIN | List all dispatchers |
| POST | /admin/compliance-officers | ADMIN | Create compliance officer |
| POST | /admin/health-officers | ADMIN | Create health officer |
| POST | /admin/ambulances | ADMIN | Add ambulance |
| GET | /admin/ambulances | ADMIN | List all ambulances |
| PATCH | /admin/ambulances/{id}/status | ADMIN | Update ambulance status |
| GET | /admin/users | ADMIN | List all users |
| PATCH | /admin/users/{id}/deactivate | ADMIN | Deactivate user |
| PATCH | /admin/users/{id}/activate | ADMIN | Activate user |

### Citizens
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | /citizens/profile | CITIZEN | Create/update profile |
| GET | /citizens/profile | CITIZEN | Get my profile |
| GET | /citizens | ADMIN | List all citizens |
| GET | /citizens/{id} | Any | Get citizen by ID |
| POST | /citizens/{id}/documents | Any | Upload document |
| GET | /citizens/{id}/documents | Any | Get citizen documents |
| PATCH | /citizens/documents/{id}/verify | ADMIN | Verify/reject document |

### Facilities
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | /facilities | ADMIN | Create facility |
| GET | /facilities | Any | List all facilities |
| GET | /facilities/{id} | Any | Get facility |
| PUT | /facilities/{id} | ADMIN | Update facility |
| PATCH | /facilities/{id}/status | ADMIN | Update facility status |
| GET | /facilities/{id}/staff | ADMIN | Get staff at facility |
| GET | /facilities/type/{type} | Any | Filter by type |
| GET | /facilities/status/{status} | Any | Filter by status |

### Compliance & Audit
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | /compliance/records | COMPLIANCE_OFFICER/ADMIN | Create compliance record |
| GET | /compliance/records | ADMIN | List all records |
| GET | /compliance/records/{id} | Any | Get record |
| GET | /compliance/records/entity/{id} | Any | Records by entity |
| GET | /compliance/records/type/{type} | Any | Records by type |
| POST | /compliance/audits | COMPLIANCE_OFFICER/ADMIN | Create audit |
| GET | /compliance/audits | ADMIN | List all audits |
| GET | /compliance/audits/{id} | Any | Get audit |
| PATCH | /compliance/audits/{id}/status | COMPLIANCE_OFFICER/ADMIN | Update audit status |
| GET | /compliance/logs | ADMIN | Get all audit logs |
| GET | /compliance/logs/user/{id} | ADMIN | Logs by user |

---

## Full Workflow

```
1. CITIZEN registers  →  POST /auth/register
2. ADMIN creates facilities, adds ambulances, creates staff
3. CITIZEN reports emergency  →  POST /emergencies/report
4. DISPATCHER views pending  →  GET /emergencies/pending
5. DISPATCHER assigns ambulance  →  POST /emergencies/{id}/dispatch
6. ADMIN views dispatched  →  GET /emergencies/dispatched
7. ADMIN admits patient  →  POST /patients/admit (ambulance auto-released!)
8. DOCTOR/NURSE assigns treatment  →  POST /treatments
9. DOCTOR/NURSE updates patient status  →  PATCH /patients/{id}/status
10. On DISCHARGED → emergency auto-CLOSED, discharge date set
```
