#!/bin/bash
export PORTAL_DATA_HOME=/data/portal-cron/cbio-portal-data
export PORTAL_HOME=/data/portal-cron
export PORTAL_CONFIG_HOME=$PORTAL_DATA_HOME/portal-configuration
export MSK_IMPACT_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/msk-impact
export MSK_RAINDANCE_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/raindance
export MSK_HEMEPACT_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/hemepact
export MSK_ARCHER_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/mskarcher
export MSK_MIXEDPACT_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/mixedpact
export MSK_KINGS_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/msk_kingscounty
export MSK_LEHIGH_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/msk_lehighvalley
export MSK_QUEENS_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/msk_queenscancercenter
export MSK_MCI_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/msk_miamicancerinstitute
export IMPACT_DATA_HOME=$PORTAL_DATA_HOME/impact
export FOUNDATION_DATA_HOME=$PORTAL_DATA_HOME/foundation
export FMI_BATLEVI_DATA_HOME=$FOUNDATION_DATA_HOME/mixed/lymphoma/mskcc/foundation/lymph_landscape_fmi_201611
export LYMPHOMA_SUPER_COHORT_DATA_HOME=$PORTAL_DATA_HOME/msk-impact/lymphoma_super_cohort_fmi_msk
# CMO_DATA_HOME looks unused
#export CMO_DATA_HOME=/data/cbio-portal-data/bic-mskcc
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk.x86_64
export PYTHON_BINARY=/data/tools/python2.7/bin/python
export HG_BINARY=/usr/bin/hg
export PATH=/home/grossb/local/bin/:$PATH:/usr/bin/:/bin/
