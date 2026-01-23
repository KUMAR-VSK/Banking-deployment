# Frontend Network Error Troubleshooting Guide

## Issue: "ERR_BLOCKED_BY_CLIENT" Network Errors

The frontend is showing network errors with the message "ERR_BLOCKED_BY_CLIENT" which indicates that browser extensions or security settings are blocking API requests to the backend.

## Common Causes

1. **Ad Blockers**: Extensions like uBlock Origin, AdBlock Plus, etc.
2. **Privacy Extensions**: Extensions like Privacy Badger, Ghostery, etc.
3. **Security Software**: Antivirus or firewall software
4. **Browser Security Settings**: Strict privacy/security settings

## Solutions

### 1. Disable Browser Extensions (Recommended First Step)

**Chrome/Edge:**
1. Click the puzzle icon in the top-right corner
2. Disable all extensions temporarily
3. Refresh the page (Ctrl+R or Cmd+R)
4. Test if the API calls work

**Firefox:**
1. Click the puzzle icon in the top-right corner
2. Disable all extensions temporarily
3. Refresh the page
4. Test if the API calls work

**Safari:**
1. Go to Safari → Preferences → Extensions
2. Uncheck all extensions
3. Refresh the page
4. Test if the API calls work

### 2. Use Incognito/Private Mode

Open the application in an incognito/private window where extensions are typically disabled by default.

### 3. Check Extension Settings

If you need to keep extensions enabled:

**uBlock Origin:**
1. Click the uBlock icon
2. Click the power button to disable for the current site
3. Or add `localhost:8080` to the whitelist

**AdBlock Plus:**
1. Click the AdBlock Plus icon
2. Click "Disable on this site"
3. Or add `localhost:8080` to the whitelist

### 4. Check CORS Configuration

The backend CORS configuration is set to allow all origins (`*`) for development. This should not be the issue, but you can verify:

1. Open browser developer tools (F12)
2. Go to Network tab
3. Make a request and check the response headers
4. Look for `Access-Control-Allow-Origin` header

### 5. Alternative: Use Different Browser

Try accessing the application in a different browser that doesn't have extensions installed.

### 6. Check for Corporate Firewall

If you're on a corporate network, there might be firewall rules blocking localhost requests. Try:
- Using a different network
- Contacting IT support

## Verification Steps

After applying any of the above solutions:

1. **Check Browser Console**: Open developer tools (F12) and check the Console tab for any remaining errors
2. **Check Network Tab**: In developer tools, go to Network tab and verify that API requests are no longer blocked
3. **Test Functionality**: Try logging in, viewing loans, and other features to ensure they work

## Expected Behavior After Fix

- No "ERR_BLOCKED_BY_CLIENT" errors in the console
- API requests should show as successful in the Network tab
- All application features should work normally
- You should be able to:
  - Register and login
  - View loan applications
  - Upload documents
  - View interest rates
  - Apply for loans

## For Production Deployment

When deploying to production (Vercel + Render), these issues should not occur as:
- Production domains are typically not blocked by ad blockers
- CORS will be properly configured between frontend and backend domains
- Users won't have localhost-specific blocking rules

## Quick Test

To quickly test if the backend is working independently:

```bash
# Test backend API directly
curl http://localhost:8080/api/manager/interest-rates

# Should return JSON data without errors
```

If this works but the frontend doesn't, the issue is definitely with browser extensions or CORS.