#include "stdafx.h"
#include "RLEKompresiCitra.h"
#include "RLEKompresiCitraDlg.h"

#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif


BEGIN_MESSAGE_MAP(CRLEKompresiCitraApp, CWinApp)

	ON_COMMAND(ID_HELP, CWinApp::OnHelp)
END_MESSAGE_MAP()


CRLEKompresiCitra::CRLKompresiCitraApp()
{

}
CRLEKompresiCitra theApp;

BOOL CRLEKompresiCitraApp::InitInstance()
{

	CRLEKompresiCitraDlg dlg;
	m_pMainWnd = &dlg;
	int nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{

	}
	else if (nResponse == IDCANCEL)
	{

	}

	return FALSE;
}
