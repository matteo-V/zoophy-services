import sys
import xml.etree.ElementTree as ET


def markov(xml_file):
    tree = ET.parse(xml_file)
    root = tree.getroot()

    generalDataTypeNodes = {}
    for node in root.findall('generalDataType'):
        name = node.attrib['id']
        name = '.'.join(name.split('.')[:-1])
        discrete_states = []
        for discrete_state in node.findall('state'):
            discrete_states.append(discrete_state.attrib['code'])
        generalDataTypeNodes[name] = discrete_states

    # Create markovJumpsTreeLikelihood nodes
    for name in generalDataTypeNodes:

        # Make math easier later
        matrix_row_length = len(generalDataTypeNodes[name])
        matrix_length = matrix_row_length**2

        markov_node = ET.SubElement(root, 'markovJumpsTreeLikelihood')
        markov_node.set('id', name + '.treeLikelihood')
        markov_node.set('stateTagName', name + '.states')
        markov_node.set('useUniformization', 'true')
        markov_node.set('saveCompleteHistory', 'true')

        attributePatterns_node = ET.SubElement(markov_node, 'attributePatterns')
        attributePatterns_node.set('idref', name + '.pattern')

        treeModel_node = ET.SubElement(markov_node, 'treeModel')
        treeModel_node.set('idref', 'treeModel')

        siteModel_node = ET.SubElement(markov_node, 'siteModel')
        siteModel_node.set('idref', name + '.siteModel')

        generalSubstitutionModel_node = ET.SubElement(markov_node, 'generalSubstitutionModel')
        generalSubstitutionModel_node.set('idref', name + '.model')

        strictClockBranchRates_node = ET.SubElement(markov_node, 'strictClockBranchRates')
        strictClockBranchRates_node.set('idref', name + '.branchRates')

        # FrequencyModel block
        frequencyModel_node = ET.SubElement(markov_node, 'frequencyModel')
        frequencyModel_node.set('id', name + '.root.frequencyModel')
        frequencyModel_node.set('normalize', 'true')

        generalDataType_node = ET.SubElement(frequencyModel_node, 'generalDataType')
        generalDataType_node.set('idref', name + '.dataType')

        frequencies_node = ET.SubElement(generalDataType_node, 'frequencies')

        parameter_node1 = ET.SubElement(frequencies_node, 'parameter')
        parameter_node1.set('id', name + '.root.frequencies')
        parameter_node1.set('dimension', str(len(generalDataTypeNodes[name])))

        # Rewards block
        rewards_node = ET.SubElement(markov_node, 'rewards')
        for i, discrete_state in enumerate(generalDataTypeNodes[name]):
            parameter_node2 = ET.SubElement(rewards_node, 'parameter')
            parameter_node2.set('id', discrete_state + '_R')

            value = ['0.0'] * matrix_row_length
            value[i] = '1.0'
            value = ' ' + ' '.join(value) + ' '
            parameter_node2.set('value', value)

        # Start ancestral state reconstruction
        parameter_node3 = ET.SubElement(markov_node, 'parameter')
        parameter_node3.set('id', name + '.count')

        value = ['1.0'] * matrix_length
        count = 0
        for i in range(matrix_row_length):
            value[i+count] = '0.0'
            count += matrix_row_length
        value = ' ' + ' '.join(value)
        parameter_node3.set('value', value)

        # Parameter blocks
        count = 0
        for i, node1 in enumerate(generalDataTypeNodes[name]):
            for j, node2 in enumerate(generalDataTypeNodes[name]):
                if i != j:
                    parameter_node4 = ET.SubElement(markov_node, 'parameter')
                    parameter_node4.set('id', node1 + '-' + node2 + '.count')

                    value = ['0.0'] * matrix_length
                    value[count] = '1.0'
                    value = ' ' + ' '.join(value) + ' '
                    parameter_node4.set('value', value)
                count += 1

        # Logs
        mcmc_node = tree.find('mcmc')
        logEvery = mcmc_node.find('log').get('logEvery')

        log_node1 = ET.SubElement(mcmc_node, 'log')
        log_node1.set('id', 'hostJumpHistory')
        log_node1.set('logEvery', logEvery)
        log_node1.set('fileName', name + '_hostJumpHistory.log')
        completeHistoryLogger = ET.SubElement(log_node1, 'completeHistoryLogger')
        markovJumpsTreeLikelihood1 = ET.SubElement(completeHistoryLogger, 'markovJumpsTreeLikelihood')
        markovJumpsTreeLikelihood1.set('idref', name + '.c')

        log_node2 = ET.SubElement(mcmc_node, 'log')
        log_node2.set('id', 'hostJumpCounts')
        log_node2.set('logEvery', logEvery)
        log_node2.set('fileName', name + '_hostJumpCounts.log')
        markovJumpsTreeLikelihood2 = ET.SubElement(log_node2, 'markovJumpsTreeLikelihood')
        markovJumpsTreeLikelihood2.set('idref', name + '.treeLikelihood')

    tree.write(xml_file)

if __name__ == '__main__':
    markov(sys.argv[1])
