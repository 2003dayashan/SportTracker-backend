## 2026-07-02T22:13:23+05:30
Establish the E2E testing track by implementing the E2E test harness and 71+ test cases across 4 tiers.

Your tasks:
1. Create a directory 'c:\Users\PrabashaBAP\Documents\GitHub\e2e-tests'.
2. Create the backend seeding JUnit test 'src/test/java/com/example/SportsTracker/SeedAdminTest.java' in the backend repository. This test must write a default admin user 'admin@test.com' with password 'password123' and role 'ROLE_ADMIN' if they do not exist.
3. Write the Node.js test file 'c:\Users\PrabashaBAP\Documents\GitHub\e2e-tests\e2e.test.js' using Node's native 'node:test' and 'node:assert' modules. It must implement exactly 71+ tests across 4 tiers:
   - Tier 1: Feature Coverage (>= 30 tests) for: User management navigation (landing page, file check for router/profile widget admin links), Paginated user list (pages, limits, sorting), Add user (different roles, verification in list, login check), Delete user (guards, removal check), Grant admin privileges (grant/revoke roles), and Security checks.
   - Tier 2: Boundary & Corner Cases (>= 30 tests) for: Self-deletion guard (400 Bad Request), Self-demotion guard (400 Bad Request), Authorization boundaries (missing/unauthorized cookies, Role checks returning 403), Invalid inputs (blank username/email/password, invalid email format, duplicate email/username, non-existent user ID, negative page numbers).
   - Tier 3: Combinatorial Flows (>= 6 tests) for: Admin CRUD workflow, Privilege escalation & demotion sequence, Self-protection validation, Pagination flow, etc.
   - Tier 4: Real-world Workloads (>= 5 tests) for: Onboarding workload, Security audit, List clean-up, Dashboard consistency, Concurrent operations simulation.
4. Write the test harness runner 'c:\Users\PrabashaBAP\Documents\GitHub\e2e-tests\runner.js' which:
   - Executes the seeding Junit test: '.\mvnw.cmd test -Dtest=SeedAdminTest' in backend directory.
   - Starts the backend Spring Boot server '.\mvnw.cmd spring-boot:run' in the background on port 8080.
   - Starts the frontend Vite dev server 'npm.cmd run dev' or 'npx.cmd vite' in the background on port 5173 in the frontend directory.
   - Waits for both servers to be fully up and running.
   - Runs 'node --test e2e.test.js'.
   - Cleans up and kills all started background processes.
   - Exits with the exit code of the test suite.
5. Run the runner.js script and verify that the tests are executed and all 71+ test cases pass successfully.
6. Provide a detailed report of the execution.
